package com.yachtmafia.handlers;

import com.google.common.util.concurrent.ListenableFuture;
import com.yachtmafia.messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.yachtmafia.util.Const.WITHDRAW_TOPIC_NAME;

public class WithdrawHandler implements MessageHandler {
    private static final String TOPIC_NAME = WITHDRAW_TOPIC_NAME;
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private final HandlerDAO handlerDAO;
    private ExecutorService pool;
    private ConsumerRecord<String, String> message;

    public WithdrawHandler(HandlerDAO handlerDAO, ExecutorService pool) {
        this.handlerDAO = handlerDAO;
        this.pool = pool;
    }

    private WithdrawHandler(HandlerDAO handlerDAO, ConsumerRecord<String, String> message) {
        this.handlerDAO = handlerDAO;
        this.message = message;
    }


    @Override
    public Future<Boolean> run(ConsumerRecord<String, String> message) {
        return pool.submit(new WithdrawHandler(handlerDAO, message));
    }

    @Override
    public Boolean call() throws Exception {
        if (TOPIC_NAME.equals(message.topic())) {
            SwapMessage swapMessage = new SwapMessage(message.value());
            LOG.info(swapMessage);

            String publicAddress = handlerDAO.getDbWrapper().getPublicAddress(swapMessage.getUsername(), swapMessage.getFromCoinName());
            if (publicAddress == null) {
                LOG.error(String.format("User: %s%nDoes not have wallet for coin: %s",
                        swapMessage.getUsername(), swapMessage.getFromCoinName()));
                return true;
            }

            String privateKey = handlerDAO.getDbWrapper().getPrivateKey(swapMessage.getUsername(), swapMessage.getFromCoinName());
            if (privateKey == null) {
                LOG.fatal(String.format("User: %s%nDoes not have private key for coin: %s",
                        swapMessage.getUsername(), swapMessage.getFromCoinName()));
                return true;
            }

            try {
                ECKey ecKey = ECKey.fromPrivate(privateKey.getBytes());
                List<ECKey> ecKeyList = new ArrayList<>();
                ecKeyList.add(ecKey);
                Wallet wallet = Wallet.fromKeys(MainNetParams.get(), ecKeyList);

                String depositAddress = handlerDAO.getExchange().getDepositAddress(swapMessage.getFromCoinName());
                Address address = Address.fromBase58(MainNetParams.get(), depositAddress);

                final Coin value = Coin.valueOf(swapMessage.getAmountOfCoin());
                // Make sure this code is run in a single thread at once.
                SendRequest request = SendRequest.to(address, value);
// The SendRequest object can be customized at this point to modify how the transaction will be created.
                wallet.completeTx(request);
// Ensure these funds won't be spent again.
                wallet.commitTx(request.tx);
//                wallet.saveToFile(...);
// A proposed transaction is now sitting in request.tx - send it in the background.
                ListenableFuture<Transaction> future = handlerDAO.getWalletAppKit().peerGroup()
                        .broadcastTransaction(request.tx).future();

// The future will complete when we've seen the transaction ripple across the network to a sufficient degree.
// Here, we just wait for it to finish, but we can also attach a listener that'll get run on a background
// thread when finished. Or we could just assume the network accepts the transaction and carry on.
                Transaction transaction = future.get();
                LOG.info(transaction);
                return true;
            } catch (Exception e) {
                LOG.error("CAUGHT", e);
                return false;
            }
        }
        return false;
    }
}
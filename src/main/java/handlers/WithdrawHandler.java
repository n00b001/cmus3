package handlers;

import bank.Bank;
import com.subgraph.orchid.encoders.Hex;
import db.DBWrapper;
import exchange.Exchange;
import messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.Wallet;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;

import static util.Const.WITHDRAW_TOPIC_NAME;

public class WithdrawHandler implements MessageHandler {
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private static final String TOPIC_NAME = WITHDRAW_TOPIC_NAME;
    private final DBWrapper dbWrapper;
    private final Bank bank;
    private final Exchange exchange;

    public WithdrawHandler(DBWrapper dbWrapper, Bank bank, Exchange exchange) {
        this.dbWrapper = dbWrapper;
        this.bank = bank;
        this.exchange = exchange;
    }

    @Override
    public boolean processMessage(ConsumerRecord<String, String> message) {
        if (TOPIC_NAME.equals(message.topic()))
        {
            SwapMessage swapMessage = new SwapMessage(message.value());
            LOG.info(swapMessage);

            String publicAddress = dbWrapper.getPublicAddress(swapMessage.getUsername(), swapMessage.getFromCoinName());
            if (publicAddress == null){
                LOG.error(String.format("User: %s\nDoes not have wallet for coin: %s",
                        swapMessage.getUsername(), swapMessage.getFromCoinName()));
                return false;
            }

            String privateKey = dbWrapper.getPrivateKey(swapMessage.getUsername(), swapMessage.getFromCoinName());
            if (privateKey == null){
                LOG.fatal(String.format("User: %s\nDoes not have private key for coin: %s",
                        swapMessage.getUsername(), swapMessage.getFromCoinName()));
                return false;
            }

            try {
                File file = new File(".");
                WalletAppKit kit = new WalletAppKit(MainNetParams.get(), file, "");
                kit.startAsync();


                final Coin value = Coin.valueOf(swapMessage.getAmountOfCoin());
                final Coin amountToSend = value.subtract(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
                String depositAddress = exchange.getDepositAddress(swapMessage.getFromCoinName());
                final Wallet.SendResult sendResult = kit.wallet().sendCoins(kit.peerGroup(),
                        Address.fromBase58(MainNetParams.get(), depositAddress), amountToSend);
                System.out.println("Sending ...");
            }catch (InsufficientMoneyException e){
                LOG.fatal("Caught error: ", e);
                return false;
            }

            return true;
        }
        return false;
    }
}

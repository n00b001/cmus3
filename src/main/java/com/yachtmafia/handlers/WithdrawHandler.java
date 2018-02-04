package com.yachtmafia.handlers;

import com.yachtmafia.messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.UnitTestParams;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.yachtmafia.util.Const.WITHDRAW_TOPIC_NAME;
import static com.yachtmafia.util.LoggerMaker.logError;
import static com.yachtmafia.util.LoggerMaker.logInfo;

public class WithdrawHandler implements MessageHandler {
    private static final String TOPIC_NAME = WITHDRAW_TOPIC_NAME;
//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());

//    private final Logger LOG = LoggerFactory.getLogger(getClass());
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
            logInfo(this, "swapMessage: " + swapMessage);

            String publicAddress = handlerDAO.getDbWrapper().getPublicAddress(swapMessage.getUsername(),
                    swapMessage.getFromCoinName());
            if (publicAddress == null) {
                logError(this, String.format("User: %s%nDoes not have wallet for coin: %s",
                        swapMessage.getUsername(), swapMessage.getFromCoinName()));
                return true;
            }

            String privateKey = handlerDAO.getDbWrapper().getPrivateKey(swapMessage.getUsername(),
                    swapMessage.getFromCoinName());
            if (privateKey == null) {
                logError(this, String.format("User: %s%nDoes not have private key for coin: %s",
                        swapMessage.getUsername(), swapMessage.getFromCoinName()));
                return true;
            }

            boolean success = handlerDAO.getWalletWrapper().sendTransaction(privateKey, publicAddress,
                    handlerDAO.getExchange().getDepositAddress(swapMessage.getFromCoinName()),
                    swapMessage.getAmountOfCoin(), MainNetParams.get());
            if (!success) {
                logError(this, "Error handing wallet to exchange transaction for: " + swapMessage.toString());
                return false;
            }

            String purchasedAmount = handlerDAO.getExchange().exchangeCurrency(swapMessage.getFromCoinName(),
                    swapMessage.getToCoinName(),
                    swapMessage.getAmountOfCoin());
            success = handlerDAO.getExchange().withdrawToBank(swapMessage.getToCoinName(), purchasedAmount);
            if (!success) {
                logError(this, "Error withdrawing from exchange to bank with message: " + swapMessage.toString()
                        + " for amount: " + purchasedAmount);
            }

            success = handlerDAO.getBank().payUser(swapMessage.getToCoinName(), purchasedAmount,
                    swapMessage.getUsername());
            if (!success) {
                logError(this, "Error when transfering from bank to user: " + swapMessage.toString() + " for amnount: "
                        + purchasedAmount);
                return false;
            }

            success = handlerDAO.getDbWrapper().addPortfolioBalance(swapMessage, purchasedAmount);
            if (!success) {
                logError(this, "Error when inserting portfoliobalance: " + swapMessage.toString() + " for amnount: "
                        + purchasedAmount);
                return false;
            }
            return true;
        }
        return false;
    }
}
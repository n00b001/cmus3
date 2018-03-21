package com.yachtmafia.handlers;

import com.yachtmafia.messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.yachtmafia.util.Const.WITHDRAW_TOPIC_NAME;

public class WithdrawHandler implements MessageHandler {
    private static final String TOPIC_NAME = WITHDRAW_TOPIC_NAME;
//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
private static final Logger logger = LogManager.getLogger(WithdrawHandler.class);

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
            logger.info("swapmessage: " + swapMessage);

            String publicAddress = handlerDAO.getDbWrapper().getPublicAddress(swapMessage.getUsername(),
                    swapMessage.getFromCoinName());
            if (publicAddress == null) {
                logger.error("user: " + swapMessage.getUsername() + " does not have wallet for coin: "
                        + swapMessage.getFromCoinName());
                return true;
            }

            String privateKey = handlerDAO.getDbWrapper().getPrivateKey(swapMessage.getUsername(),
                    swapMessage.getFromCoinName());
            if (privateKey == null) {
                logger.error("user: " + swapMessage.getUsername() + " Does not have private key for coin: "
                        + swapMessage.getFromCoinName());
                return true;
            }

            boolean success = handlerDAO.getWalletWrapper().sendTransaction(privateKey, publicAddress,
                    handlerDAO.getExchange().getDepositAddress(swapMessage.getFromCoinName()),
                    swapMessage.getAmountOfCoin(), handlerDAO.getNetwork());
            if (!success) {
                logger.error("Error handling wallet to exchange transaction for: " + swapMessage.toString());
                return false;
            }

            String purchasedAmount = handlerDAO.getExchange().exchangeCurrency(swapMessage.getFromCoinName(),
                    swapMessage.getToCoinName(),
                    swapMessage.getAmountOfCoin());
            success = handlerDAO.getExchange().withdrawToBank(swapMessage.getToCoinName(), purchasedAmount);
            if (!success) {
                logger.error("Error withdrawing from exchange to bank with message; " + swapMessage
                        + " for amount: " + purchasedAmount);
            }

            success = handlerDAO.getBank().payUser(swapMessage.getToCoinName(), purchasedAmount,
                    swapMessage.getUsername());
            if (!success) {
                logger.error("error when transfering from bank to user: " + swapMessage.toString()
                        + " for amount: " + purchasedAmount);
                return false;
            }

            success = handlerDAO.getDbWrapper().addPortfolioBalance(swapMessage, purchasedAmount);
            if (!success) {
                logger.error("error when inserting portfoliobalance: " + swapMessage.toString()
                        + " for amount " + purchasedAmount);
                return false;
            }
            return true;
        }
        return false;
    }
}
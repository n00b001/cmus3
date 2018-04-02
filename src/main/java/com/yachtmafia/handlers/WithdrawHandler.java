package com.yachtmafia.handlers;

import com.yachtmafia.messages.SwapMessage;
import com.yachtmafia.util.StatusLookup;
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
            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.REQUEST_RECEIVED_BY_SERVER);


            String publicAddress = handlerDAO.getDbWrapper().getPublicAddress(swapMessage.getUsername(),
                    swapMessage.getFromCoinName());
            if (publicAddress == null) {
                logger.error("user: " + swapMessage.getUsername() + " does not have wallet for coin: "
                        + swapMessage.getFromCoinName());
                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.COULD_NOT_FIND_WALLET);

                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.FAILED);
                return true;
            }

            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.WALLET_FOUND);

            String privateKey = handlerDAO.getDbWrapper().getPrivateKey(swapMessage.getUsername(),
                    swapMessage.getFromCoinName());
            if (privateKey == null) {
                logger.error("user: " + swapMessage.getUsername() + " Does not have private key for coin: "
                        + swapMessage.getFromCoinName());
                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.COULD_NOT_FIND_PRIVATE_KEY);
                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.FAILED);
                return true;
            }
            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.PRIVATE_KEY_FOUND);


            String depositAddress = handlerDAO.getExchange().getDepositAddress(swapMessage.getFromCoinName());
            boolean success = handlerDAO.getWalletWrapper().sendBitcoinTransaction(privateKey, publicAddress,
                    depositAddress, swapMessage.getAmountOfCoin(), handlerDAO.getNetwork());
            if (!success) {
                logger.error("Error handling wallet to exchange transaction for: " + swapMessage.toString());
                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.WALLET_TO_EXCHANGE_TRANSACTION_FAILURE);
                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.FAILED);
                return false;
            }
            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.COINS_SENT_TO_EXCHANGE);

            String purchasedAmount = handlerDAO.getExchange().exchangeCurrency(swapMessage.getFromCoinName(),
                    swapMessage.getToCoinName(),
                    swapMessage.getAmountOfCoin());
            success = handlerDAO.getExchange().withdrawToBank(swapMessage.getToCoinName(), purchasedAmount);
            if (!success) {
                logger.error("Error withdrawing from exchange to bank with message; " + swapMessage
                        + " for amount: " + purchasedAmount);
                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.FAILED_TO_WITHDRAW_FROM_EXCHANGE);
                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.FAILED);
                return false;
            }
            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.MONEY_WITHDRAWN_FROM_EXCHANGE);

            success = handlerDAO.getBank().payUser(swapMessage.getToCoinName(), purchasedAmount,
                    swapMessage.getUsername());
            if (!success) {
                logger.error("error when transfering from bank to user: " + swapMessage.toString()
                        + " for amount: " + purchasedAmount);
                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.COULD_NOT_PAY_USER);
                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.FAILED);
                return false;
            }

            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.PAID_USER);
            success = handlerDAO.getDbWrapper().addPortfolioBalance(swapMessage, purchasedAmount);
            if (!success) {
                logger.error("error when inserting portfoliobalance: " + swapMessage.toString()
                        + " for amount " + purchasedAmount);
                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.COULD_NOT_UPDATE_PORTFOLIO_BALANCE);
                handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.FAILED);

                return false;
            }
            handlerDAO.getDbWrapper().addTransactionStatus(swapMessage, StatusLookup.SUCCESS);
            return true;
        }
        return false;
    }
}
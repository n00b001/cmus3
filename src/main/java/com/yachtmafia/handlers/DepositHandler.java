package com.yachtmafia.handlers;

import com.yachtmafia.cryptoKeyPairs.CryptoKeyPair;
import com.yachtmafia.cryptoKeyPairs.CryptoKeyPairGenerator;
import com.yachtmafia.messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.yachtmafia.util.Const.DEPOSIT_TOPIC_NAME;

public class DepositHandler implements MessageHandler {
    private static final String TOPIC_NAME = DEPOSIT_TOPIC_NAME;
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private final HandlerDAO handlerDAO;
    private ExecutorService pool;
    private ConsumerRecord<String, String> message;

    public DepositHandler(HandlerDAO handlerDAO, ExecutorService pool) {
        this.handlerDAO = handlerDAO;
        this.pool = pool;
    }

    private DepositHandler(HandlerDAO handlerDAO, ConsumerRecord<String, String> message) {
        this.handlerDAO = handlerDAO;
        this.message = message;
    }

    @Override
    public Future<Boolean> run(ConsumerRecord<String, String> message) {
        return pool.submit(new DepositHandler(handlerDAO, message));
    }

    @Override
    public Boolean call() throws Exception {
        if (TOPIC_NAME.equals(message.topic())) {
            SwapMessage swapMessage = new SwapMessage(message.value());
            LOG.info(swapMessage);
            String publicAddress = handlerDAO.getDbWrapper().getPublicAddress(swapMessage.getUsername(),
                    swapMessage.getToCoinName());
            if (publicAddress == null){
                try {
                    CryptoKeyPair keyPair = CryptoKeyPairGenerator.parse(swapMessage.getToCoinName());
                    boolean success = handlerDAO.getDbWrapper().addNewWallet(swapMessage.getUsername(), swapMessage.getToCoinName(),
                            keyPair.getPublicAddress(), keyPair.getPrivateKey());
                    if (!success) {
                        LOG.fatal("Did not add wallet successfully! " + message.toString());
                        return false;
                    }
                    publicAddress = keyPair.getPublicAddress();
                }catch (Exception e){
                    LOG.fatal("Did not add wallet successfully! " + message.toString(), e);
                    return false;
                }
            }
            boolean success = handlerDAO.getBank().transferFromBankToExchange(swapMessage.getFromCoinName(),
                    swapMessage.getAmountOfCoin());
            if (!success){
                LOG.fatal("Did not transfer balance from com.yachtmafia.bank to com.yachtmafia.exchange! " + message.toString());
                return false;
            }
            long purchasedAmount = handlerDAO.getExchange().exchangeCurrency(swapMessage.getFromCoinName(), swapMessage.getToCoinName(),
                    swapMessage.getAmountOfCoin());

            success = handlerDAO.getExchange().withdraw(swapMessage.getToCoinName(), publicAddress, purchasedAmount);
            if (!success){
                LOG.fatal("Did not withdraw coins! " + message.toString());
                return false;
            }
            success = handlerDAO.getDbWrapper().addPortfolioBalance(swapMessage, purchasedAmount);
            if (!success){
                LOG.fatal("Did not add portfolio balance! " + message.toString());
                return false;
            }
            return true;
        }
        return false;
    }
}

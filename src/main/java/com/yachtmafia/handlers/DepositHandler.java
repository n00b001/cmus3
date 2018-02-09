package com.yachtmafia.handlers;

import com.yachtmafia.cryptoKeyPairs.CryptoKeyPair;
import com.yachtmafia.cryptoKeyPairs.CryptoKeyPairGenerator;
import com.yachtmafia.messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bitcoinj.params.MainNetParams;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.yachtmafia.util.Const.DEPOSIT_TOPIC_NAME;
import static com.yachtmafia.util.LoggerMaker.logError;
import static com.yachtmafia.util.LoggerMaker.logInfo;

public class DepositHandler implements MessageHandler {
    private static final String TOPIC_NAME = DEPOSIT_TOPIC_NAME;
//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());

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
            logInfo(this, "swapMessage: " + swapMessage.toString());
            String publicAddress = handlerDAO.getDbWrapper().getPublicAddress(swapMessage.getUsername(),
                    swapMessage.getToCoinName());
            if (publicAddress == null){
                try {
                    CryptoKeyPair keyPair = CryptoKeyPairGenerator.parse(swapMessage.getToCoinName(),
                            MainNetParams.get());
                    boolean success = handlerDAO.getDbWrapper().addNewWallet(swapMessage.getUsername(),
                            swapMessage.getToCoinName(),
                            keyPair.getPublicAddress(), keyPair.getPrivateKey());
                    if (!success) {
                        logError(this, "Did not add wallet successfully! "+ message);
                        return false;
                    }
                    publicAddress = keyPair.getPublicAddress();
                }catch (Exception e){
                    logError(this, "Did not add wallet successfully! " + message.toString(), e);
                    return false;
                }
            }
            boolean success = handlerDAO.getBank().transferFromBankToExchange(swapMessage.getFromCoinName(),
                    swapMessage.getAmountOfCoin(), handlerDAO.getExchange());
            if (!success){
                logError(this, "Did not transfer balance from com.yachtmafia.bank to com.yachtmafia.exchange! "
                         + message);
                return false;
            }
            String purchasedAmount = handlerDAO.getExchange().exchangeCurrency(swapMessage.getFromCoinName(),
                    swapMessage.getToCoinName(), swapMessage.getAmountOfCoin());
            if(purchasedAmount == null){
                logError(this, "Failed to make purchase: " + message.toString());
            }

            success = handlerDAO.getExchange().withdrawCrypto(swapMessage.getToCoinName(), publicAddress,
                    purchasedAmount);
            if (!success){
                logError(this, "Did not withdraw coins! " + message);
                return false;
            }
            success = handlerDAO.getDbWrapper().addPortfolioBalance(swapMessage, purchasedAmount);
            if (!success){
                logError(this, "Did not add portfolio balance! " + message);
                return false;
            }
            return true;
        }
        return false;
    }
}

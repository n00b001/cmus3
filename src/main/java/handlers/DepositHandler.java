package handlers;

import cryptoKeyPairs.CryptoKeyPair;
import cryptoKeyPairs.CryptoKeyPairGetter;
import db.DBWrapper;
import messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;

import static util.Const.DEPOSIT_TOPIC_NAME;

public class DepositHandler implements MessageHandler {
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private static final String TOPIC_NAME = DEPOSIT_TOPIC_NAME;
    private final DBWrapper dbWrapper;

    public DepositHandler(DBWrapper dbWrapper) {
        this.dbWrapper = dbWrapper;
    }

    @Override
    public boolean processMessage(ConsumerRecord<String, String> message) {
        if (TOPIC_NAME.equals(message.topic())) {
            SwapMessage swapMessage = new SwapMessage(message.value());
            LOG.info(swapMessage);
            String publicAddress = dbWrapper.getPublicAddress(swapMessage.getUsername(),
                    swapMessage.getToCoinName());
            if (publicAddress == null){
                try {
                    CryptoKeyPair keyPair = createKeyPair(swapMessage.getToCoinName());
                    boolean success = dbWrapper.addNewWallet(swapMessage.getUsername(), swapMessage.getToCoinName(),
                            keyPair.getPublicAddress(), keyPair.getPrivateAddress());
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
            boolean success = transferFromBankToExchange();
            if (!success){
                LOG.fatal("Did not transfer balance from bank to exchange! " + message.toString());
                return false;
            }
            success = exchangeCoins(swapMessage.getFromCoinName(), swapMessage.getToCoinName());
            if (!success){
                LOG.fatal("Did not exchange coins! " + message.toString());
                return false;
            }
            success = transferToWallet(publicAddress);
            if (!success){
                LOG.fatal("Did not transfer coins! " + message.toString());
                return false;
            }



            return true;
        }
        return false;
    }

    private boolean transferToWallet(String publicAddress) {
        return false;
    }

    private boolean exchangeCoins(String fromCurrencyName, String toCurrencyName) {
        return false;
    }

    private boolean transferFromBankToExchange() {
        return false;
    }

    private CryptoKeyPair createKeyPair(String toCoinName) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        return CryptoKeyPairGetter.parse(toCoinName);
    }
}

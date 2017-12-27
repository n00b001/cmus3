package handlers;

import bank.Bank;
import cryptoKeyPairs.CryptoKeyPair;
import cryptoKeyPairs.CryptoKeyPairGenerator;
import db.DBWrapper;
import exchange.Exchange;
import messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static util.Const.DEPOSIT_TOPIC_NAME;

public class DepositHandler implements MessageHandler {
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private static final String TOPIC_NAME = DEPOSIT_TOPIC_NAME;
    private final DBWrapper dbWrapper;
    private final Bank bank;
    private final Exchange exchange;

    public DepositHandler(DBWrapper dbWrapper, Bank bank, Exchange exchange) {
        this.dbWrapper = dbWrapper;
        this.bank = bank;
        this.exchange = exchange;
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
                    CryptoKeyPair keyPair = CryptoKeyPairGenerator.parse(swapMessage.getToCoinName());
                    boolean success = dbWrapper.addNewWallet(swapMessage.getUsername(), swapMessage.getToCoinName(),
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
            boolean success = bank.transferFromBankToExchange(swapMessage.getFromCoinName(),
                    swapMessage.getAmountOfCoin());
            if (!success){
                LOG.fatal("Did not transfer balance from bank to exchange! " + message.toString());
                return false;
            }
            success = exchange.exchangeCurrency(swapMessage.getFromCoinName(), swapMessage.getToCoinName(),
                    swapMessage.getAmountOfCoin());
            if (!success){
                LOG.fatal("Did not exchange coins! " + message.toString());
                return false;
            }
            success = exchange.withdraw(swapMessage.getToCoinName(), publicAddress);
            if (!success){
                LOG.fatal("Did not transfer coins! " + message.toString());
                return false;
            }
            throw new NotImplementedException();
        }
        return false;
    }
}

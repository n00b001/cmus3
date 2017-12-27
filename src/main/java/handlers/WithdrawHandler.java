package handlers;

import bank.Bank;
import com.subgraph.orchid.encoders.Hex;
import db.DBWrapper;
import exchange.Exchange;
import messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.params.MainNetParams;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

            Address address = new Address(MainNetParams.get(), Hex.decode(privateKey));
            System.out.println(address);
//            throw new NotImplementedException();

            return true;
        }
        return false;
    }
}

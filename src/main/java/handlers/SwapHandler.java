package handlers;

import bank.Bank;
import db.DBWrapper;
import exchange.Exchange;
import messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import static util.Const.SWAP_TOPIC_NAME;

public class SwapHandler implements MessageHandler {
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private static final String TOPIC_NAME = SWAP_TOPIC_NAME;
    private final DBWrapper dbWrapper;
    private final Bank bank;
    private final Exchange exchange;

    public SwapHandler(DBWrapper dbWrapper, Bank bank, Exchange exchange) {
        this.dbWrapper = dbWrapper;
        this.bank = bank;
        this.exchange = exchange;
    }

    @Override
    public boolean processMessage(ConsumerRecord<String, String> message) {
        if (TOPIC_NAME.equals(message.topic())){
            SwapMessage swapMessage = new SwapMessage(message.value());
            LOG.info(swapMessage);
            throw new NotImplementedException();
        }
        return false;
    }
}

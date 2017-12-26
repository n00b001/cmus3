package handlers;

import db.DBWrapper;
import messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;

import static util.Const.WITHDRAW_TOPIC_NAME;

public class WithdrawHandler implements MessageHandler {
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private static final String TOPIC_NAME = WITHDRAW_TOPIC_NAME;
    private final DBWrapper dbWrapper;

    public WithdrawHandler(DBWrapper dbWrapper) {
        this.dbWrapper = dbWrapper;
    }

    @Override
    public boolean processMessage(ConsumerRecord<String, String> message) {
        if (TOPIC_NAME.equals(message.topic()))
        {
            SwapMessage swapMessage = new SwapMessage(message.value());
            LOG.info(swapMessage);
            return true;
        }
        return false;
    }
}

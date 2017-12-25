package handlers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;

public class DepositHandler implements MessageHandler {
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private static final String TOPIC_NAME = "DEPOSIT";
    @Override
    public boolean processMessage(ConsumerRecord<String, String> message) {
        if (TOPIC_NAME.equals(message.topic())) {
            LOG.info(message);
            return true;
        }
        return false;
    }
}

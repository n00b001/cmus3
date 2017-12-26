package handlers;

import messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;

public class SwapHandler implements MessageHandler {
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private static final String TOPIC_NAME = "SWAP";
    @Override
    public boolean processMessage(ConsumerRecord<String, String> message) {
        if (TOPIC_NAME.equals(message.topic())){
            SwapMessage withdrawMessage = new SwapMessage(message.value());
            LOG.info(withdrawMessage);
            return true;
        }
        return false;
    }
}

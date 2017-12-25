package handlers;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface MessageHandler {
    boolean processMessage(ConsumerRecord<String, String> message);
}

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class DepositHandler implements MessageHandler {
    @Override
    public boolean processMessage(ConsumerRecord<String, String> message) {
        return false;
    }
}

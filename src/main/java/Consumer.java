import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Consumer implements Runnable{

    //kafka Consumer object
    private KafkaConsumer<String, String> consumer;
    private volatile boolean running = true;
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private final List<MessageHandler> listeners;

    public Consumer(List<MessageHandler> listeners){
        this.listeners = listeners;
    }

    public void configure(Properties props){
        LOG.info("Configuring Consumer...");
        consumer = new KafkaConsumer<>(props);
    }

    public void subscribe(List<String> topics){
        //subscribe to topic

        LOG.info(String.format("Subscribing to: %s", topics.toString()));
        consumer.subscribe(topics);
    }

    public void stop(){
        LOG.info("Stopping Consumer...");
        running = false;
    }

    @Override
    public void run() {
        LOG.info("Starting Consumer...");
        while (running && !Thread.currentThread().isInterrupted()) {
            ConsumerRecords<String, String> records = consumer.poll(0);
            for (ConsumerRecord<String, String> record : records) {
                for (MessageHandler handler : listeners){
                    handler.processMessage(record);
                }
//                LOG.info(String.format("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value()));
//                System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
            }
            consumer.commitAsync();
            consumer.commitSync();
        }
    }
}

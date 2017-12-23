import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.Properties;

public class main {

    public static void main(String[] args) {

        //consumer properties
        Properties props = new Properties();
        props.put("bootstrap.servers", "35.197.252.186:9092");
        props.put("group.id", "test-group");

        //using auto commit
        props.put("enable.auto.commit", "true");

        //string inputs and outputs
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        //kafka consumer object
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        //subscribe to topic
        consumer.subscribe(Arrays.asList("my-topic"));

        //infinite poll loop
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
            }
        }

    }

}

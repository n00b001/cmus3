import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Main {

    private Consumer consumer;
    private Producer producer = new Producer();
    private List<Thread> threads = new ArrayList<>();
    private volatile boolean running = true;
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private List<MessageHandler> listeners;

    public static void main(String[] args) {

        Main Main = new Main();
        Main.run();
    }

    public void run(){
        setupListeners();
        startConsumer();
//        startProducer();
        startAllThreads();
        waitForThreadsToFinish();
    }

    private void setupListeners() {
        listeners = new ArrayList<>();
        listeners.add(new DepositHandler());
    }

    private void startAllThreads() {
        for (Thread t : threads){
            t.start();
        }
    }

    private void startProducer() {
        configureProducer();

        threads.add(new Thread(producer));
    }

    private void configureProducer() {
        //consumer properties
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "35.197.252.186:9092");
//        props.put("group.id", "test-group");

        //string inputs and outputs
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
                , "org.apache.kafka.common.serialization.StringSerializer");

        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, "100");
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "100");

        producer.configure(props);
        producer.subscribe("my-topic");
    }

    private void waitForThreadsToFinish() {
        try {
            while (running && !Thread.interrupted()) {
                for(Thread t : threads){
                    if (t.isInterrupted() || !t.isAlive()) {
                        running = false;
                        break;
                    }
                }
                Thread.sleep(1000);
            }
        }
        catch (InterruptedException ex){
            LOG.error("Caught error! ", ex);
        }
        LOG.info("Shutting down...");
        consumer.stop();
        producer.stop();
        for(Thread t : threads){
            t.interrupt();
        }
    }

    private void startConsumer() {
        configureConsumer();

        threads.add(new Thread(consumer));
    }

    private void configureConsumer() {
        //consumer properties
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                "35.197.252.186:2181");
                "35.197.252.186:9092");
        props.put("group.id", "test-group");

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        //string inputs and outputs
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        //subscribe to topic
        List<String> conf = Arrays.asList("my-topic");
        consumer = new Consumer(listeners);
        consumer.configure(props);
        consumer.subscribe(conf);
    }

}

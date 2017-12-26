import db.DBWrapper;
import db.DBWrapperImpl;
import handlers.DepositHandler;
import handlers.MessageHandler;
import handlers.SwapHandler;
import handlers.WithdrawHandler;
import kafka.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static util.Const.*;

public class Main implements Thread.UncaughtExceptionHandler{

//    private kafka.Consumer consumer = new kafka.Consumer();
//    private kafka.Producer producer = new kafka.Producer();
    private List<Thread> threads = new ArrayList<>();
    private volatile boolean running = true;
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private DBWrapper dbWrapper;
//    private List<handlers.MessageHandler> listeners;

    public static void main(String[] args) {

        Main Main = new Main();
        Main.run();
    }

    public void run(){
//        setupListeners();
        setupDBWrapper();
        setupConsumers();
        setupUnhandledExceptions();
//        startProducer();
        startAllThreads();
        waitForThreadsToFinish();
    }

    private void setupDBWrapper() {
        dbWrapper = new DBWrapperImpl();
    }

    private void setupUnhandledExceptions() {
        for (Thread t : threads){
            t.setUncaughtExceptionHandler(this);
        }
    }

//    private void setupListeners() {
//        listeners = new ArrayList<>();
//        listeners.add(new handlers.DepositHandler());
//        listeners.add(new handlers.WithdrawHandler());
//        listeners.add(new handlers.SwapHandler());
//    }

    private void startAllThreads() {
        for (Thread t : threads){
            t.start();
        }
    }

//    private void startProducer() {
//        configureProducer();
//
//        threads.add(new Thread(producer));
//    }

//    private void configureProducer() {
//        //consumer properties
//        Properties props = new Properties();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                "35.197.252.186:9092");
////        props.put("group.id", "test-group");
//
//        //string inputs and outputs
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
//                "org.apache.kafka.common.serialization.StringSerializer");
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
//                , "org.apache.kafka.common.serialization.StringSerializer");
//
//        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, "100");
//        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, "100");
//
//        producer.configure(props);
//        producer.subscribe("my-topic");
//    }

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
        for(Thread t : threads){
            t.interrupt();
        }
    }

    private void setupConsumers() {
        List<MessageHandler> depositListers = new ArrayList<>();
        depositListers.add(new DepositHandler(dbWrapper));

        List<MessageHandler> withdrawListers = new ArrayList<>();
        withdrawListers.add(new WithdrawHandler(dbWrapper));

        List<MessageHandler> swapListers = new ArrayList<>();
        swapListers.add(new SwapHandler(dbWrapper));


        Consumer depositConsumer = configureConsumer(Arrays.asList(DEPOSIT_TOPIC_NAME), depositListers);
        Consumer withdrawConsumer = configureConsumer(Arrays.asList(WITHDRAW_TOPIC_NAME), withdrawListers);
        Consumer swapConsumer = configureConsumer(Arrays.asList(SWAP_TOPIC_NAME), swapListers);

        threads.add(new Thread(depositConsumer));
        threads.add(new Thread(withdrawConsumer));
        threads.add(new Thread(swapConsumer));
    }

    private Consumer configureConsumer(List<String> topics, List<MessageHandler> listeners) {
        //consumer properties
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                "35.197.252.186:2181");
                "35.197.252.186:9092");
        // This is the ID of this consumer machine
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "1");

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        //string inputs and outputs
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
//        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        //subscribe to topic
        Consumer consumer = new Consumer();
        consumer.configure(props, listeners);
        consumer.subscribe(topics);
        return consumer;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LOG.error(String.format("Caught from thread: %s: ", t.toString()), e);
    }
}

package com.yachtmafia;

import com.yachtmafia.bank.Bank;
import com.yachtmafia.bank.BankImpl;
import com.yachtmafia.config.Config;
import com.yachtmafia.db.DBWrapper;
import com.yachtmafia.db.DBWrapperImpl;
import com.yachtmafia.exchange.Exchange;
import com.yachtmafia.exchange.ExchangeWrapper;
import com.yachtmafia.handlers.*;
import com.yachtmafia.kafka.Consumer;
import com.yachtmafia.walletwrapper.WalletWrapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.yachtmafia.util.Const.*;
import static com.yachtmafia.util.LoggerMaker.logError;
import static com.yachtmafia.util.LoggerMaker.logInfo;

public class Main implements Thread.UncaughtExceptionHandler{

//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());

//    private final Logger LOG = LoggerFactory.getLogger(getClass());
    //    private Consumer consumer = new Consumer();
//    private Producer producer = new Producer();
    private List<Thread> threads = new ArrayList<>();
    private volatile boolean running = true;
    private DBWrapper dbWrapper;
    private Bank bank;
    private Exchange exchange;
    private HandlerDAO handlerDAO;
    private Config config;
    private WalletWrapper walletWrapper;

    public static void main(String[] args) {

        Main Main = new Main();
        Main.run();
    }

    private void run(){
        setupConfig();
        setupDBWrapper();
        setupBank();
        setupExchange();
        setupWalletWrapper();
        setupConsumers();
        setupUnhandledExceptions();
//        startProducer();
        startAllThreads();
        waitForThreadsToFinish();
    }

    private void setupWalletWrapper() {
        walletWrapper = new WalletWrapper();
    }

    private void setupConfig() {
        this.config = new Config();
    }

    private void setupExchange() {
        exchange = new ExchangeWrapper(config);
    }

    private void setupBank() {
        bank = new BankImpl(config);
    }

    private void setupDBWrapper() {
        dbWrapper = new DBWrapperImpl(config);
    }

    private void setupUnhandledExceptions() {
        for (Thread t : threads){
            t.setUncaughtExceptionHandler(this);
        }
    }

    private void startAllThreads() {
        walletWrapper.startAsync();
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
            logError(getClass(), "Caught error! ", ex);
            Thread.currentThread().interrupt();
        }
        logInfo(getClass(), "Shutting down...");
        for(Thread t : threads){
            t.interrupt();
        }
    }

    private void setupConsumers() {
        handlerDAO = new HandlerDAO(dbWrapper, bank, exchange, walletWrapper);

        List<MessageHandler> depositListers = new ArrayList<>();
        ExecutorService handlerPool = Executors.newFixedThreadPool(3);
        depositListers.add(new DepositHandler(handlerDAO, handlerPool));

        List<MessageHandler> withdrawListers = new ArrayList<>();
        ExecutorService withdrawPool = Executors.newFixedThreadPool(3);
        withdrawListers.add(new WithdrawHandler(handlerDAO, withdrawPool));

        List<MessageHandler> swapListers = new ArrayList<>();
        ExecutorService swapPool = Executors.newFixedThreadPool(3);
        swapListers.add(new SwapHandler(handlerDAO, swapPool));


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
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.KAFKA_ADDRESS);
        // This is the ID of this consumer machine
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "1");

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        //string inputs and outputs
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");

        //subscribe to topic
        Consumer consumer = new Consumer(props, listeners);
        consumer.subscribe(topics);
        return consumer;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logError(getClass(), String.format("Caught from thread: %s: ", t.toString()), e);
    }
}

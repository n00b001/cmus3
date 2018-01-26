package com.yachtmafia.kafka;

import com.yachtmafia.handlers.MessageHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.log4j.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Future;

import static com.yachtmafia.util.LoggerMaker.logError;
import static com.yachtmafia.util.LoggerMaker.logInfo;

public class Consumer implements Runnable{

//    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private Set<Future<Boolean>> set = new HashSet<>();
    //kafka Consumer object
    private KafkaConsumer<String, String> consumer;
    private volatile boolean running = true;
    private List<MessageHandler> listeners;

    public Consumer(Properties props, List<MessageHandler> listeners){
        logInfo(getClass(), "Configuring Consumer...");
        this.listeners = listeners;
        consumer = new KafkaConsumer<>(props);
    }

    public void subscribe(List<String> topics){
        //subscribe to topic

        logInfo(getClass(), String.format("Subscribing to: %s", topics.toString()));
        consumer.subscribe(topics);
    }

    public void stop(){
        logInfo(getClass(), "Stopping Consumer...");
        running = false;
    }

    @Override
    public void run() {
        logInfo(getClass(), "Starting Consumer...");
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(0);
                for (ConsumerRecord<String, String> record : records) {
                    for (MessageHandler handler : listeners) {
                        callListener(record, handler);
                    }
//                logInfo(getClass(), String.format("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value()));
//                System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
                }
                Boolean success = getSuccess();
                set.clear();
                commitOnSuccess(records, success);
            } catch (InterruptedException e) {
//                logError(getClass(), "Caught exception: ", e);
                running = false;
                Thread.currentThread().interrupt();
            }catch (Exception e) {
                logError(getClass(), "Caught exception: ", e);
            }
        }

    }

    private void callListener(ConsumerRecord<String, String> record, MessageHandler handler) {
        try {
//                        boolean successfullyProcessed = handler.processMessage(record);
            Future<Boolean> future = handler.run(record);
            set.add(future);
        } catch (Exception e) {
            logError(getClass(), "Caught exception: ", e);
            consumer.commitAsync();
        }
    }

    private void commitOnSuccess(ConsumerRecords<String, String> records, Boolean success) {
        if (success == null) {
            /**
             * Do nothing
             */
        } else if (!success) {
            logError(getClass(), "Could not process: ");
            for (ConsumerRecord<String, String> msg : records) {
                logError(getClass(), msg.value());
            }
        } else {
            consumer.commitSync();
//                        consumer.commitSync();
        }
    }

    private Boolean getSuccess() throws InterruptedException, java.util.concurrent.ExecutionException {
        Boolean success = null;
        for (Future<Boolean> fut : set) {
            success = (success == null ? true : success) && fut.get();
        }
        return success;
    }
}

package com.yachtmafia.kafka;

import com.yachtmafia.handlers.MessageHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Future;

public class Consumer implements Runnable{

    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private Set<Future<Boolean>> set = new HashSet<>();
    //kafka Consumer object
    private KafkaConsumer<String, String> consumer;
    private volatile boolean running = true;
    private List<MessageHandler> listeners;

    public Consumer(Properties props, List<MessageHandler> listeners){
        LOG.info("Configuring Consumer...");
        this.listeners = listeners;
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
            try {
                ConsumerRecords<String, String> records = consumer.poll(0);
                for (ConsumerRecord<String, String> record : records) {
                    for (MessageHandler handler : listeners) {
                        callListener(record, handler);
                    }
//                LOG.info(String.format("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value()));
//                System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
                }
                Boolean success = getSuccess();
                set.clear();
                commitOnSuccess(records, success);
            } catch (Exception e) {
                LOG.error("Caught exception: ", e);
            }
        }

    }

    private void callListener(ConsumerRecord<String, String> record, MessageHandler handler) {
        try {
//                        boolean successfullyProcessed = handler.processMessage(record);
            Future<Boolean> future = handler.run(record);
            set.add(future);
        } catch (Exception e) {
            LOG.error("Caught exception: ", e);
            consumer.commitAsync();
        }
    }

    private void commitOnSuccess(ConsumerRecords<String, String> records, Boolean success) {
        if (success == null) {
            /**
             * Do nothing
             */
        } else if (!success) {
            LOG.error("Could not process: ");
            for (ConsumerRecord<String, String> msg : records) {
                LOG.error(msg.value());
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

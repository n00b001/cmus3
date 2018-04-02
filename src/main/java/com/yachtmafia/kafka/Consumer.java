package com.yachtmafia.kafka;

import com.yachtmafia.handlers.MessageHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.Arrays;

//import org.apache.log4j.Logger;

public class Consumer implements Runnable{

//    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private static final Logger logger = LogManager.getLogger(Consumer.class);
    private Set<Future<Boolean>> threadsOfFutures = new HashSet<>();
    //kafka Consumer object
    private KafkaConsumer<String, String> consumer;
    private volatile boolean running = true;
    private List<MessageHandler> listeners;

    public Consumer(Properties props, List<MessageHandler> listeners){
        logger.info("Configuring consumer...");
        this.listeners = listeners;
        consumer = new KafkaConsumer<>(props);
    }

    public void subscribe(String topics){
        //subscribe to topic

        logger.info("Subscribing to: " + topics);
        consumer.subscribe(Arrays.asList(topics));
    }

    public void stop(){
        logger.info("Stopping Consumer...");

        running = false;
    }

    @Override
    public void run() {
        logger.info("Starting consumer...");
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(250);
                for (ConsumerRecord<String, String> record : records) {
                    for (MessageHandler handler : listeners) {
                        callListener(record, handler);
                    }
//                logInfo(getClass(), String.format("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value()));
//                System.out.printf("offset = %d, key = %s, value = %s\n", record.offset(), record.key(), record.value());
                }
                /**
                 * todo: Commit only on success, BUT don't block other people's payments
                 */
//                Boolean success = getSuccess();
//                commitOnSuccess(records, success);
                threadsOfFutures.clear();
//            } catch (InterruptedException e) {
////                logError(getClass(), "Caught exception: ", e);
//                running = false;
//                Thread.currentThread().interrupt();
            }catch (Exception e) {
                logger.error("Caught: ", e);
            }
        }

    }

    private void callListener(ConsumerRecord<String, String> record, MessageHandler handler) {
        try {
//                        boolean successfullyProcessed = handler.processMessage(record);
            Future<Boolean> future = handler.run(record);
            threadsOfFutures.add(future);
        } catch (Exception e) {
            logger.error("Caught: ", e);
            consumer.commitAsync();
        }
    }

    private void commitOnSuccess(ConsumerRecords<String, String> records, Boolean success) {
        if (success == null) {
            /**
             * Do nothing
             */
        } else if (!success) {
            logger.error("Could not process: ");
            for (ConsumerRecord<String, String> msg : records) {
                logger.error(msg.value());
            }
        } else {
            consumer.commitSync();
//                        consumer.commitSync();
        }
    }

    private Boolean getSuccess() throws InterruptedException, java.util.concurrent.ExecutionException {
        Boolean success = null;
        for (Future<Boolean> fut : threadsOfFutures) {
            success = (success == null ? true : success) && fut.get();
        }
        return success;
    }
}

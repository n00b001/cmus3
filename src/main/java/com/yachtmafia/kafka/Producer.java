package com.yachtmafia.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.yachtmafia.util.LoggerMaker.logError;
import static com.yachtmafia.util.LoggerMaker.logInfo;

public class Producer implements Runnable{

//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());

//    private final Logger LOG = LoggerFactory.getLogger(getClass());
    //kafka Consumer object
    private KafkaProducer<String, String> producer;
    private volatile boolean running = true;
    private String topic;

    public void configure(Properties props){
        logInfo(getClass(), "Configuring producer...");
        producer = new KafkaProducer<>(props);
    }

    void subscribe(String topic) {
        this.topic = topic;
    }

    void stop() {
        logInfo(getClass(), "Stopping producer...");
        running = false;
    }

    @Override
    public void run() {
        logInfo(getClass(), "Starting producer...");
        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                try {
                    long time = System.currentTimeMillis();
                    int sendMessageCount = 100;
                    for (long index = time; index < time + sendMessageCount; index++) {
                        final ProducerRecord<String, String> record
                                = new ProducerRecord<>(topic, String.valueOf(index), "Hello Mom " + index);

                        send(time, record);
                        Thread.sleep(1000);

                    }
                } catch (ExecutionException e) {
                    logError(getClass(), "Caught error: ", e);
                }
            }
        }
        catch (InterruptedException ex){
            logError(getClass(), "Caught exception: ", ex);
            Thread.currentThread().interrupt();
        }finally {
            producer.flush();
            producer.close();
        }
    }

    public void send(long time, ProducerRecord<String, String> record) throws InterruptedException, ExecutionException {
        RecordMetadata metadata = producer.send(record).get();

        long elapsedTime = System.currentTimeMillis() - time;
        logInfo(getClass(), String.format("sent record(key=%s value=%s) " +
                        "meta(partition=%d, offset=%d) time=%d\n",
                record.key(), record.value(), metadata.partition(),
                metadata.offset(), elapsedTime));
    }
}

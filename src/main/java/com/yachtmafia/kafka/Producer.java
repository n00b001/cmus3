package com.yachtmafia.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Producer implements Runnable{

    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    //kafka Consumer object
    private KafkaProducer<String, String> producer;
    private volatile boolean running = true;
    private String topic;

    public void configure(Properties props){
        LOG.info("Configuring producer...");
        producer = new KafkaProducer<>(props);
    }

    void subscribe(String topic) {
        this.topic = topic;
    }

    void stop() {
        LOG.info("Stopping producer...");
        running = false;
    }

    @Override
    public void run() {
        LOG.info("Starting producer...");
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
                    LOG.error("Caught error: ", e);
                }
            }
        }
        catch (InterruptedException ex){
            LOG.error("Caught exception: ", ex);
            Thread.currentThread().interrupt();
        }finally {
            producer.flush();
            producer.close();
        }
    }

    public void send(long time, ProducerRecord<String, String> record) throws InterruptedException, ExecutionException {
        RecordMetadata metadata = producer.send(record).get();

        long elapsedTime = System.currentTimeMillis() - time;
        LOG.info(String.format("sent record(key=%s value=%s) " +
                        "meta(partition=%d, offset=%d) time=%d\n",
                record.key(), record.value(), metadata.partition(),
                metadata.offset(), elapsedTime));
    }
}

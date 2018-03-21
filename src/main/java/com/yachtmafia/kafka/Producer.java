package com.yachtmafia.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;
import java.util.concurrent.ExecutionException;


public class Producer implements Runnable{

//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
private static final Logger logger = LogManager.getLogger(Producer.class);

//    private final Logger LOG = LoggerFactory.getLogger(getClass());
    //kafka Consumer object
    private KafkaProducer<String, String> producer;
    private volatile boolean running = true;
    private String topic;

    public void configure(Properties props){
        logger.info("Configuring producer...");
        producer = new KafkaProducer<>(props);
    }

    void subscribe(String topic) {
        this.topic = topic;
    }

    void stop() {
        logger.info("Stopping producer...");
        running = false;
    }

    @Override
    public void run() {
        logger.info("Starting producer...");
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
                    logger.error("Caught: ", e);
                }
            }
        }
        catch (InterruptedException ex){
            logger.error("Caught: ", ex);
            Thread.currentThread().interrupt();
        }finally {
            producer.flush();
            producer.close();
        }
    }

    public void send(long time, ProducerRecord<String, String> record) throws InterruptedException, ExecutionException {
        RecordMetadata metadata = producer.send(record).get();

        long elapsedTime = System.currentTimeMillis() - time;
        logger.info("send record(key=" + record.key() + " value="+record.value()
                + " meta(partition=" + metadata.partition() + ", offset=" + metadata.offset()
                + ") time=" + elapsedTime + "\n");
    }
}

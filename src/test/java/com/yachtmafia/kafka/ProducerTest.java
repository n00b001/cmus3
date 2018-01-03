package com.yachtmafia.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

/**
 * Created by xfant on 2018-01-02.
 */
public class ProducerTest {
    private Producer producer;

    @Before
    public void setup() {
        producer = new Producer();
    }

    @Test
    public void configure() throws Exception {
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
    }

    @Test
    public void subscribe() throws Exception {
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

    @Test
    public void stop() throws Exception {
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

        Thread thread = new Thread(producer);
        thread.start();
        producer.stop();
        while (thread.isAlive()) {
            Thread.sleep(100);
        }
        assert (!thread.isAlive());
    }

    @Test
    public void run() throws Exception {
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

        Thread thread = new Thread(producer);
        thread.start();
        assert (thread.isAlive());
    }


}
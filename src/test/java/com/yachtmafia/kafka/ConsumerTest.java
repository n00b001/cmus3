package com.yachtmafia.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by xfant on 2018-01-04.
 */
public class ConsumerTest {
    Consumer consumer;

    @Test
    @Ignore
    public void run() throws Exception {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                "35.197.252.186:9092");
        // This is the ID of this consumer machine
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "1");

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        //string inputs and outputs
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                "org.apache.kafka.common.serialization.StringDeserializer");


        consumer = new Consumer(props, null);
        consumer.subscribe(Arrays.asList("my-topic"));
        consumer.run();
    }

}
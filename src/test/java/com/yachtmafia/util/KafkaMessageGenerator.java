package com.yachtmafia.util;

import com.yachtmafia.kafka.Producer;
import com.yachtmafia.messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.yachtmafia.util.Const.*;

public class KafkaMessageGenerator {
    public static List<ConsumerRecord<String, String>> getDepositMessages(long amountOfMessage){
        List<ConsumerRecord<String, String>> records = new ArrayList<>();

        for (long i=0;i<amountOfMessage;i++) {
            String value = "{'"+ SwapMessage.AMOUNT_OF_COIN_ATTRIB+"':'"+i+"', '"
                    +SwapMessage.FROM_CURRENCY_NAME_ATTRIB+"':'GBP', '"
                    +SwapMessage.TO_CURRENCY_NAME_ATTRIB+"':'BTC', '"+
                    SwapMessage.USERNAME_ATTRIB + "':'PAMILA@gmail.com'}";
            records.add(new ConsumerRecord<>(DEPOSIT_TOPIC_NAME, 0, 0, null, value));
        }
        return records;
    }

    public static List<ConsumerRecord<String, String>> getWithdrawMessages(long amountOfMessage){
        List<ConsumerRecord<String, String>> records = new ArrayList<>();

        for (long i = 0; i < amountOfMessage * 1000; i += 1000) {
            String value = "{'"+ SwapMessage.AMOUNT_OF_COIN_ATTRIB+"':'"+i+"', '"
                    +SwapMessage.FROM_CURRENCY_NAME_ATTRIB+"':'BTC', '"
                    +SwapMessage.TO_CURRENCY_NAME_ATTRIB+"':'GBP', '"+
                    SwapMessage.USERNAME_ATTRIB + "':'PAMILA@gmail.com'}";
            records.add(new ConsumerRecord<>(WITHDRAW_TOPIC_NAME, 0, 0, null, value));
        }
        return records;
    }

    public static List<ConsumerRecord<String, String>> getSwapMessages(long amountOfMessage){
        List<ConsumerRecord<String, String>> records = new ArrayList<>();

        for (long i=0;i<amountOfMessage;i++) {
            String value = "{'"+ SwapMessage.AMOUNT_OF_COIN_ATTRIB+"':'"+i+"', '"
                    +SwapMessage.FROM_CURRENCY_NAME_ATTRIB+"':'BTC', '"
                    +SwapMessage.TO_CURRENCY_NAME_ATTRIB+"':'ETH', '"+
                    SwapMessage.USERNAME_ATTRIB + "':'PAMILA@gmail.com'}";
            records.add(new ConsumerRecord<>(SWAP_TOPIC_NAME, 0, 0, null, value));
        }
        return records;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int amountOfMessage = 1;
        List<ConsumerRecord<String, String>> swapMessages = getSwapMessages(amountOfMessage);
        List<ConsumerRecord<String, String>> depositMessages = getDepositMessages(amountOfMessage);
        List<ConsumerRecord<String, String>> withdrawMessages = getWithdrawMessages(amountOfMessage);

        Producer producer = new Producer();
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
//        for (ConsumerRecord<String, String> entry: swapMessages) {
//            producer.send(System.currentTimeMillis() / 1000, new ProducerRecord<>(
//                    SWAP_TOPIC_NAME, entry.key(), entry.value()));
//        }

        for (ConsumerRecord<String, String> entry : depositMessages) {
            producer.send(System.currentTimeMillis() / 1000, new ProducerRecord<>(
                    DEPOSIT_TOPIC_NAME, entry.key(), entry.value()));
        }
//        for (ConsumerRecord<String, String> entry: withdrawMessages) {
//            producer.send(System.currentTimeMillis() / 1000, new ProducerRecord<>(
//                    WITHDRAW_TOPIC_NAME, entry.key(), entry.value()));
//        }
    }
}

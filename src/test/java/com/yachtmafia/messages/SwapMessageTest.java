package com.yachtmafia.messages;

import com.google.gson.JsonSyntaxException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Before;
import org.junit.Test;

import static com.yachtmafia.util.KafkaMessageGenerator.getDepositMessages;
import static org.junit.Assert.*;

/**
 * Created by xfant on 2018-01-14.
 */
public class SwapMessageTest {
    SwapMessage swapMessage;

    @Test
    public void badJson() {
        boolean value = false;
        try {
            swapMessage = new SwapMessage("asdf");
        }catch (IllegalStateException e){
            value = true;
        }
        assert value;
    }

    @Test
    public void goodMessage() throws Exception {
        boolean value = true;
        try {
            ConsumerRecord<String, String> consumerRecord = getDepositMessages(1).get(0);
            swapMessage = new SwapMessage(consumerRecord.value());
            swapMessage.getToCoinName();
            swapMessage.getAmountOfCoin();
            swapMessage.getUsername();
            swapMessage.getFromCoinName();
        }catch (JsonSyntaxException e){
            System.out.println(e);
            value = false;
        }
        assert value;
    }
}
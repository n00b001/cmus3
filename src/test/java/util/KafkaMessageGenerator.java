package util;

import messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.ArrayList;
import java.util.List;

import static util.Const.DEPOSIT_TOPIC_NAME;
import static util.Const.SWAP_TOPIC_NAME;
import static util.Const.WITHDRAW_TOPIC_NAME;

public class KafkaMessageGenerator {
    public static List<ConsumerRecord<String, String>> getDepositMessages(long amountOfMessage){
        List<ConsumerRecord<String, String>> records = new ArrayList<>();

        for (long i=0;i<amountOfMessage;i++) {
            String value = "{'"+ SwapMessage.AMOUNT_OF_COIN_ATTRIB+"':'"+i+"', '"
                    +SwapMessage.FROM_CURRENCY_NAME_ATTRIB+"':'GBP', '"
                    +SwapMessage.TO_CURRENCY_NAME_ATTRIB+"':'BTC', '"+
                    SwapMessage.USERNAME_ATTRIB+"':'testUser'}";
            records.add(new ConsumerRecord<>(DEPOSIT_TOPIC_NAME, 0, 0, null, value));
        }
        return records;
    }

    public static List<ConsumerRecord<String, String>> getWithdrawMessages(long amountOfMessage){
        List<ConsumerRecord<String, String>> records = new ArrayList<>();

        for (long i=0;i<amountOfMessage;i++) {
            String value = "{'"+ SwapMessage.AMOUNT_OF_COIN_ATTRIB+"':'"+i+"', '"
                    +SwapMessage.FROM_CURRENCY_NAME_ATTRIB+"':'BTC', '"
                    +SwapMessage.TO_CURRENCY_NAME_ATTRIB+"':'GBP', '"+
                    SwapMessage.USERNAME_ATTRIB+"':'testUser'}";
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
                    SwapMessage.USERNAME_ATTRIB+"':'testUser'}";
            records.add(new ConsumerRecord<>(SWAP_TOPIC_NAME, 0, 0, null, value));
        }
        return records;
    }
}

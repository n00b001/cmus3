package handlers;

import bank.Bank;
import bank.BankMock;
import db.DBWrapper;
import db.DBWrapperMock;
import exchange.Exchange;
import exchange.ExchangeMock;
import messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static util.Const.DEPOSIT_TOPIC_NAME;
import static util.KafkaMessageGenerator.getDepositMessages;
import static util.KafkaMessageGenerator.getSwapMessages;
import static util.KafkaMessageGenerator.getWithdrawMessages;

public class DepositHandlerTest {
    private MessageHandler messageHandler;

    @Before
    public void setup(){
        DBWrapper dbWrapper = new DBWrapperMock();
        Bank bank = new BankMock();
        Exchange exchange = new ExchangeMock();
        messageHandler = new DepositHandler(dbWrapper, bank, exchange);
    }

    @Test
    public void processMessage() {
        List<ConsumerRecord<String, String>> records = getDepositMessages(100);

        for (ConsumerRecord<String, String> cr : records) {
            assert messageHandler.processMessage(cr);
        }

        records = getWithdrawMessages(100);

        for (ConsumerRecord<String, String> cr : records) {
            assert !messageHandler.processMessage(cr);
        }

        records = getSwapMessages(100);

        for (ConsumerRecord<String, String> cr : records) {
            assert !messageHandler.processMessage(cr);
        }
    }
}
package handlers;

import bank.Bank;
import bank.BankMock;
import db.DBWrapper;
import db.DBWrapperMock;
import exchange.Exchange;
import exchange.ExchangeMock;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static util.KafkaMessageGenerator.getDepositMessages;
import static util.KafkaMessageGenerator.getSwapMessages;
import static util.KafkaMessageGenerator.getWithdrawMessages;

public class WithdrawHandlerTest {
    private MessageHandler messageHandler;

    @Before
    public void setup(){
        DBWrapper dbWrapper = new DBWrapperMock();
        Bank bank = new BankMock();
        Exchange exchange = new ExchangeMock();
        messageHandler = new WithdrawHandler(dbWrapper, bank, exchange);
    }

    @Test
    public void processMessage() {
        List<ConsumerRecord<String, String>> records = getWithdrawMessages(100);

        for (ConsumerRecord<String, String> cr : records) {
            assert messageHandler.processMessage(cr);
        }

        records = getDepositMessages(100);

        for (ConsumerRecord<String, String> cr : records) {
            assert !messageHandler.processMessage(cr);
        }

        records = getSwapMessages(100);

        for (ConsumerRecord<String, String> cr : records) {
            assert !messageHandler.processMessage(cr);
        }
    }
}
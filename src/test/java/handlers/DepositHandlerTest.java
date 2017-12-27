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
        List<ConsumerRecord<String, String>> records = new ArrayList<>();
        String value = "{'"+ SwapMessage.AMOUNT_OF_COIN_ATTRIB+"':'100', '"
                +SwapMessage.FROM_CURRENCY_NAME_ATTRIB+"':'GBP', '"
                +SwapMessage.TO_CURRENCY_NAME_ATTRIB+"':'BTC', '"+
                SwapMessage.USERNAME_ATTRIB+"':'testUser'}";
        records.add(new ConsumerRecord<>(DEPOSIT_TOPIC_NAME, 0, 0, null, value));

        for (ConsumerRecord<String, String> cr : records) {
            messageHandler.processMessage(cr);
        }
    }
}
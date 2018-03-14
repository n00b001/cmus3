package com.yachtmafia.bank;

import com.yachtmafia.config.Config;
import com.yachtmafia.exchange.Exchange;
import com.yachtmafia.exchange.ExchangeMock;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by xfant on 2018-01-08.
 */
public class BankImplTest {
    BankImpl bank;

    @Test
    public void payUser() throws Exception {
        bank = new BankImpl(new Config());
        String currency = "GBP";
        String amount = String.valueOf(100);
        String user = "pamila@gmail.com";
        boolean success = bank.payUser(currency, amount, user);
        assert success;
    }

    @Test @Ignore
    public void toExchange() throws Exception {
        bank = new BankImpl(new Config());
        Exchange exchange = new ExchangeMock();
        String currency = "GBP";
        String amount = String.valueOf(100);
        boolean success = bank.transferFromBankToExchange(currency, amount, exchange);
        assert success;
    }
}
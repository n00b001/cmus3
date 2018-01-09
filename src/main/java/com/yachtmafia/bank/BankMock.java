package com.yachtmafia.bank;

import com.yachtmafia.exchange.Exchange;

public class BankMock implements Bank {
    @Override
    public boolean transferFromBankToExchange(String currency, long amount, Exchange exchange) {
        return true;
    }

    @Override
    public boolean payUser(String currency, long amount, String user) {
        return true;
    }
}

package com.yachtmafia.bank;

import com.yachtmafia.exchange.Exchange;

public class BankMock implements Bank {
    @Override
    public boolean transferFromBankToExchange(String currency, String amount, Exchange exchange) {
        return true;
    }

    @Override
    public boolean payUser(String currency, String amount, String user) {
        return true;
    }
}

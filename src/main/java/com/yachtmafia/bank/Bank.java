package com.yachtmafia.bank;

import com.yachtmafia.exchange.Exchange;

public interface Bank {
    boolean transferFromBankToExchange(String currency, String amount, Exchange exchange);

    boolean payUser(String currency, String amount, String user);
}

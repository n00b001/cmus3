package com.yachtmafia.bank;

import com.yachtmafia.exchange.Exchange;

public interface Bank {
    boolean transferFromBankToExchange(String currency, long amount, Exchange exchange);

    boolean payUser(String currency, long amount, String user);
}

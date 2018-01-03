package com.yachtmafia.bank;

public interface Bank {
    boolean transferFromBankToExchange(String currency, long amount);

    boolean payUser(String currency, long amount, String user);
}

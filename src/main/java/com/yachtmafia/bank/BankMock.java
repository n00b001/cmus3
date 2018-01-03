package com.yachtmafia.bank;

public class BankMock implements Bank {
    @Override
    public boolean transferFromBankToExchange(String currency, long amount) {
        return true;
    }

    @Override
    public boolean payUser(String currency, long amount, String user) {
        return true;
    }
}

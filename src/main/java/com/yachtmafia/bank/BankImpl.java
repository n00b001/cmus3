package com.yachtmafia.bank;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BankImpl implements Bank {
    @Override
    public boolean transferFromBankToExchange(String currency, long amount) {
        throw new NotImplementedException();
    }

    @Override
    public boolean payUser(String currency, long amount, String user) {
        throw new NotImplementedException();
    }
}

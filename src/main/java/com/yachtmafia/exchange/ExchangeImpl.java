package com.yachtmafia.exchange;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ExchangeImpl implements Exchange {
    @Override
    public long exchangeCurrency(String from, String to, long amount) {
        throw new NotImplementedException();
    }

    @Override
    public boolean withdrawCrypto(String coinName, String address, long amount) {
        throw new NotImplementedException();
    }

    @Override
    public String getDepositAddress(String fromCoinName) {
        throw new NotImplementedException();
    }

    @Override
    public boolean withdrawToBank(String toCoinName, long purchasedAmount) {
        throw new NotImplementedException();
    }
}

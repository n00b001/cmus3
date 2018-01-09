package com.yachtmafia.exchange;

public interface Exchange {
    long exchangeCurrency(String from, String to, long amount);

    boolean withdrawCrypto(String coinName, String address, long amount);

    String getDepositAddress(String fromCoinName);

    boolean withdrawToBank(String toCoinName, long purchasedAmount);
}

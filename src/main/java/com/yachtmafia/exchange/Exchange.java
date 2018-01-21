package com.yachtmafia.exchange;

import java.util.Set;

public interface Exchange {
    String exchangeCurrency(String from, String to, String amount);

    boolean withdrawCrypto(String coinName, String address, String amount);

    String getDepositAddress(String fromCoinName);

    boolean withdrawToBank(String toCoinName, String purchasedAmount);

    Set<String> getAvailableCoins();

    String getLowestPrice(String symbolPair);

    String getHighestPrice(String symbolPair);
}

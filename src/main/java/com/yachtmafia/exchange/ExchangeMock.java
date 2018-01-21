package com.yachtmafia.exchange;

import com.yachtmafia.cryptoKeyPairs.BTC;

import java.util.HashSet;
import java.util.Set;

public class ExchangeMock implements Exchange {
    @Override
    public String exchangeCurrency(String from, String to, String amount) {
        return amount;
    }

    @Override
    public boolean withdrawCrypto(String coinName, String address, String amount) {
        return true;
    }

    @Override
    public String getDepositAddress(String fromCoinName) {
        return new BTC().getPublicAddress();
    }

    @Override
    public boolean withdrawToBank(String toCoinName, String purchasedAmount) {
        return true;
    }

    @Override
    public Set<String> getAvailableCoins() {
        Set<String> coins = new HashSet<>();
        coins.add("BTC");
        coins.add("ETH");
        return coins;
    }

    @Override
    public String getLowestPrice(String symbolPair) {
        return "1";
    }

    @Override
    public String getHighestPrice(String symbolPair) {
        return "1234";
    }
}

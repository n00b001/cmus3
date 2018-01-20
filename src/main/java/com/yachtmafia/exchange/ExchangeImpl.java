package com.yachtmafia.exchange;

import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.yachtmafia.config.Config;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;

public class ExchangeImpl implements Exchange {
    private Config config;
    private Coinbase cb;

    public ExchangeImpl(Config config) {
        this.config = config;
        cb = new CoinbaseBuilder()
                .withApiKey(config.COINBASE_KEY, config.COINBASE_SECRET)
                .build();
    }

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

    @Override
    public Set<String> getAvailableCoins() {
        throw new NotImplementedException();
    }

    @Override
    public String getLowestPrice(String symbolPair) {
        throw new NotImplementedException();
    }

    @Override
    public String getHighestPrice(String symbolPair) {
        throw new NotImplementedException();
    }
}

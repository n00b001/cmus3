package com.yachtmafia.exchange;

import com.google.api.gax.rpc.UnimplementedException;
import com.yachtmafia.config.Config;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;

/**
 * Created by xfant on 2018-02-08.
 */
public class ExchangeGdax implements Exchange {
    private final Config config;
    private final;

    public ExchangeGdax(Config config) {
        this.config = config;
    }

    @Override
    public String exchangeCurrency(String from, String to, String amount) {
        throw new NotImplementedException();
    }

    @Override
    public boolean withdrawCrypto(String coinName, String address, String amount) {
        throw new NotImplementedException();
    }

    @Override
    public String getDepositAddress(String fromCoinName) {
        throw new NotImplementedException();
    }

    @Override
    public boolean withdrawToBank(String toCoinName, String purchasedAmount) {
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

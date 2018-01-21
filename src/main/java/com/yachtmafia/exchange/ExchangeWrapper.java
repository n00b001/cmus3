package com.yachtmafia.exchange;

import com.yachtmafia.config.Config;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.Set;

public class ExchangeWrapper implements Exchange {
    private Config config;
    Set<Exchange> exchanges = new HashSet<>();
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());

    public ExchangeWrapper(Config config) {
        this.config = config;
    }

    public void addExchange(Exchange e){
        exchanges.add(e);
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
        Set<String> returnList = new HashSet<>();
        for (Exchange e : exchanges) {
            returnList.addAll(e.getAvailableCoins());
        }
        return returnList;
    }

    @Override
    public String getLowestPrice(String symbolPair) {
        throw new NotImplementedException();
    }

    @Override
    public String getHighestPrice(String symbolPair) {
        throw new NotImplementedException();
    }


    public Config getConfig() {
        return config;
    }
}

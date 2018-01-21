package com.yachtmafia.exchange;

import com.yachtmafia.config.Config;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Comparator;
import java.util.HashSet;
import java.util.OptionalLong;
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
        OptionalLong min = exchanges
                .stream()
                .mapToLong(a -> Long.parseLong(a.getLowestPrice(symbolPair)))
                .min();

        String returnVal = null;
        if (min.isPresent()){
            returnVal = String.valueOf(min.getAsLong());
        }
        return returnVal;
    }
    @Override
    public String getHighestPrice(String symbolPair) {
        OptionalLong max = exchanges
                .stream()
                .mapToLong(a -> Long.parseLong(a.getHighestPrice(symbolPair)))
                .max();

        String returnVal = null;
        if (max.isPresent()){
            returnVal = String.valueOf(max.getAsLong());
        }
        return returnVal;
    }


    public Config getConfig() {
        return config;
    }
}

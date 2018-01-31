package com.yachtmafia.exchange;

import com.yachtmafia.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.OptionalLong;
import java.util.Set;

public class ExchangeWrapper implements Exchange {
    private Config config;
    Set<Exchange> exchanges = new HashSet<>();
//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());


    public ExchangeWrapper(Config config) {
        this.config = config;
    }

    public void addExchange(Exchange e){
        exchanges.add(e);
    }

    @Override
    public String exchangeCurrency(String from, String to, String amount) {
        for (Exchange exchange : exchanges) {
            String purchaseAmount = exchange.exchangeCurrency(from, to, amount);
            if (purchaseAmount != null){
                return purchaseAmount;
            }
        }
        return null;
    }

    @Override
    public boolean withdrawCrypto(String coinName, String address, String amount) {
        for (Exchange exchange : exchanges) {
            boolean success = exchange.withdrawCrypto(coinName, address, amount);
            if (success){
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDepositAddress(String fromCoinName) {
        for (Exchange exchange : exchanges) {
            String depositAddress = exchange.getDepositAddress(fromCoinName);
            if (depositAddress != null){
                return depositAddress;
            }
        }
        return null;
    }

    @Override
    public boolean withdrawToBank(String toCoinName, String purchasedAmount) {
        for (Exchange exchange : exchanges) {
            boolean success = exchange.withdrawToBank(toCoinName, purchasedAmount);
            if (success){
                return true;
            }
        }
        return false;
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

package com.yachtmafia.exchange;

import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.coinbase.api.exception.CoinbaseException;
import com.yachtmafia.config.Config;
import org.apache.log4j.Logger;
import org.joda.money.CurrencyUnit;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xfant on 2018-01-20.
 */
public class ExchangeCoinbase implements Exchange {
    private Coinbase cb;
    private Config config;
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());

    public ExchangeCoinbase(Config config) {
        this.config = config;
//        cb = new CoinbaseBuilder()
//                .withApiKey(config.COINBASE_KEY, config.COINBASE_SECRET)
//                .build();

        cb = new CoinbaseBuilder()
                .withAccessToken("4a9cb0ba78434e59c5371fb388f928db72e8cd407dd2f6590f6a47a3a672cd39")
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
        Set<String> returnSet = new HashSet<>();
        try {
            cb.getSupportedCurrencies().forEach(
                    a -> returnSet.add(a.getSymbol()));
        } catch (CoinbaseException| IOException e) {
            LOG.error("Caught: " + e);
        }
        return returnSet;
    }

    @Override
    public String getLowestPrice(String symbolPair) {
        String returnString = "";
        throw new NotImplementedException();
//        try {
////            cb.getOrders()
//        } catch (CoinbaseException| IOException e) {
//            LOG.error("Caught: " + e);
//        }
//        return returnString;
    }

    @Override
    public String getHighestPrice(String symbolPair) {
        return null;
    }
}

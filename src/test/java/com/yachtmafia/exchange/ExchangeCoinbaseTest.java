package com.yachtmafia.exchange;

import com.yachtmafia.config.Config;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by xfant on 2018-01-20.
 */
public class ExchangeCoinbaseTest {
    ExchangeCoinbase exchangeCoinbase;

    @Before
    public void setUp()  {
        Config config = new Config();
        exchangeCoinbase = new ExchangeCoinbase(config);
    }

    @Test
    public void exchangeCurrency()  {
        String from = "GBP";
        String to = "BTC";
        String amount = "100";
        exchangeCoinbase.exchangeCurrency(from, to, amount);
    }

    @Test
    public void withdrawCrypto() {
        String coinName = "BTC";
        String address = "";
        String amount = "100000000";
        exchangeCoinbase.withdrawCrypto(coinName, address, amount);
    }

    @Test
    public void getDepositAddress() {
        String fromCoinName = "BTC";
        exchangeCoinbase.getDepositAddress(fromCoinName);
    }

    @Test
    public void withdrawToBank()  {
        String toCoinName = "GBP";
        String purchasedAMount = "100";
        exchangeCoinbase.withdrawToBank(toCoinName, purchasedAMount);
    }

    @Test
    public void getAvailableCoins()  {
        Set<String> availableCoins = exchangeCoinbase.getAvailableCoins();
        assert !availableCoins.isEmpty();
    }

    @Test
    public void getLowest() throws Exception {
        String symbolPair = "BTCGBP";
        String lowestPrice = exchangeCoinbase.getLowestPrice(symbolPair);
        assert lowestPrice != null;
    }

    @Test
    public void getHighest() throws Exception {
        String symbolPair = "BTCGBP";
        String highestPrice = exchangeCoinbase.getHighestPrice(symbolPair);
        assert highestPrice != null;
    }
}
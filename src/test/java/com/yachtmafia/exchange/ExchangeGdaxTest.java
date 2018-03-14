package com.yachtmafia.exchange;

import com.yachtmafia.config.Config;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by xfant on 2018-02-11.
 */
@Ignore
public class ExchangeGdaxTest {
    ExchangeGdax exchangeGdax;

    @Before
    public void setUp() throws Exception {
        Config config = new Config();
        exchangeGdax = new ExchangeGdax(config);
    }

    @Test
    public void exchangeCurrency() throws Exception {
        String from = "GBP";
        String to = "BTC";
        String amount = "1000";
        String amountPurchased = exchangeGdax.exchangeCurrency(from, to, amount);
        assert amountPurchased != null;
        assert !"".equals(amountPurchased);
    }

    @Test
    public void withdrawCrypto() throws Exception {
        String coinName = "BTC";
        String address = "";
        String amount = "1000";
        boolean success = exchangeGdax.withdrawCrypto(coinName, address, amount);
        assert success;
    }

    @Test
    public void getDepositAddress() throws Exception {
        String fromCoinName = "BTC";
        String depositAddress = exchangeGdax.getDepositAddress(fromCoinName);
        assert depositAddress != null;
        assert !"".equals(depositAddress);
    }

    @Test
    public void withdrawToBank() throws Exception {
        String toCoinName = "GBP";
        String purchasedAMount = "1000";
        boolean success = exchangeGdax.withdrawToBank(toCoinName, purchasedAMount);
        assert success;
    }

    @Test
    public void getAvailableCoins() throws Exception {
        Set<String> availableCoins = exchangeGdax.getAvailableCoins();
        assert availableCoins != null;
        assert !availableCoins.isEmpty();
    }

    @Test
    public void getLowestPrice() throws Exception {
        String symbolPair = "BTCGBP";
        String lowestPrice = exchangeGdax.getLowestPrice(symbolPair);
        assert lowestPrice != null;
    }

    @Test
    public void getHighestPrice() throws Exception {
        String symbolPair = "BTCGBP";
        String highestPrice = exchangeGdax.getHighestPrice(symbolPair);
        assert highestPrice != null;
    }

}
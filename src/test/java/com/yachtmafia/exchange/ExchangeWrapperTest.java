package com.yachtmafia.exchange;

import com.yachtmafia.config.Config;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

/**
 * Created by xfant on 2018-01-14.
 */
public class ExchangeWrapperTest {
    Exchange exchange;
    @Before
    public void setUp() throws Exception {
        Config config = new Config();
        exchange = new ExchangeWrapper(config);
    }

    @Test
    public void exchangeCurrency() throws Exception {
        String from = "GBP";
        String to = "BTC";
        long amount = 10000; // 100gbp
        long purchaseAmount = exchange.exchangeCurrency(from, to, amount);

        from = "BTC";
        to = "GBP";
        amount = 100000000; // 1btc
        purchaseAmount = exchange.exchangeCurrency(from, to, amount);
    }

    @Test
    public void withdrawCrypto() throws Exception {
        String coinName = "BTC";
        String address = "";
        long amount = 100000000; // 1btc
        boolean success = exchange.withdrawCrypto(coinName, address, amount);
        assert success;
    }

    @Test
    public void getDepositAddress() throws Exception {
        String coinName = "BTC";
        String depositAddress = exchange.getDepositAddress(coinName);
        assert depositAddress != null;
    }

    @Test
    public void withdrawToBank() throws Exception {
        String toCoinName = "BTC";
        long purchasedAmount = 100000000; // 1btc
        boolean success = exchange.withdrawToBank(toCoinName, purchasedAmount);
        assert success;
    }

    @Test
    public void getAvailableCoins() throws Exception {
        ExchangeWrapper exchange = (ExchangeWrapper) this.exchange;
        exchange.addExchange(new ExchangeMock());
        Set<String> availableCoins = this.exchange.getAvailableCoins();
        assert !availableCoins.isEmpty();
    }
}
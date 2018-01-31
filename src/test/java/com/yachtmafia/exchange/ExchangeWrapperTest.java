package com.yachtmafia.exchange;

import com.yachtmafia.config.Config;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Set;

/**
 * Created by xfant on 2018-01-14.
 */
@Ignore
public class ExchangeWrapperTest {
    ExchangeWrapper exchangeWrapper;
    @Before
    public void setUp() throws Exception {
        Config config = new Config();
        exchangeWrapper = new ExchangeWrapper(config);
        exchangeWrapper.addExchange(new ExchangeMock());
    }

    @Test
    public void exchangeCurrency() throws Exception {
        String from = "GBP";
        String to = "BTC";
        String amount = String.valueOf(10000); // 100gbp
        String purchaseAmount = exchangeWrapper.exchangeCurrency(from, to, amount);

        from = "BTC";
        to = "GBP";
        amount = String.valueOf(100000000); // 1btc
        purchaseAmount = exchangeWrapper.exchangeCurrency(from, to, amount);
    }

    @Test
    public void withdrawCrypto() throws Exception {
        String coinName = "BTC";
        String address = "";
        String amount = String.valueOf(100000000); // 1btc
        boolean success = exchangeWrapper.withdrawCrypto(coinName, address, amount);
        assert success;
    }

    @Test
    public void getDepositAddress() throws Exception {
        String coinName = "BTC";
        String depositAddress = exchangeWrapper.getDepositAddress(coinName);
        assert depositAddress != null;
    }

    @Test
    public void withdrawToBank() throws Exception {
        String toCoinName = "BTC";
        String purchasedAmount = String.valueOf(100000000); // 1btc
        boolean success = exchangeWrapper.withdrawToBank(toCoinName, purchasedAmount);
        assert success;
    }

    @Test
    public void getAvailableCoins() throws Exception {
        ExchangeWrapper exchange = (ExchangeWrapper) this.exchangeWrapper;
        exchange.addExchange(new ExchangeMock());
        Set<String> availableCoins = this.exchangeWrapper.getAvailableCoins();
        assert !availableCoins.isEmpty();
    }


    @Test
    public void getLowest() throws Exception {
        String symbolPair = "BTCGBP";
        String lowestPrice = exchangeWrapper.getLowestPrice(symbolPair);
        assert lowestPrice != null;
    }

    @Test
    public void getHighest() throws Exception {
        String symbolPair = "BTCGBP";
        String highestPrice = exchangeWrapper.getHighestPrice(symbolPair);
        assert highestPrice != null;
    }
}
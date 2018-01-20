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
    public void setUp() throws Exception {
        Config config = new Config();
        exchangeCoinbase = new ExchangeCoinbase(config);
    }

    @Test
    public void exchangeCurrency() throws Exception {
    }

    @Test
    public void withdrawCrypto() throws Exception {
    }

    @Test
    public void getDepositAddress() throws Exception {
    }

    @Test
    public void withdrawToBank() throws Exception {
    }

    @Test
    public void getAvailableCoins() throws Exception {
        Set<String> availableCoins = exchangeCoinbase.getAvailableCoins();
        assert !availableCoins.isEmpty();
    }

}
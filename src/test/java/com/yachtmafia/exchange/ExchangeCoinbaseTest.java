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
    }

    @Test
    public void withdrawCrypto() {
    }

    @Test
    public void getDepositAddress() {
    }

    @Test
    public void withdrawToBank()  {
    }

    @Test
    public void getAvailableCoins()  {
        Set<String> availableCoins = exchangeCoinbase.getAvailableCoins();
        assert !availableCoins.isEmpty();
    }

}
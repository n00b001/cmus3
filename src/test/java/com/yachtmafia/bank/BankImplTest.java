package com.yachtmafia.bank;

import com.yachtmafia.config.Config;
import org.junit.Test;

/**
 * Created by xfant on 2018-01-08.
 */
public class BankImplTest {
    BankImpl bank;

    @Test
    public void payUser() throws Exception {
        bank = new BankImpl(new Config());
        String currency = "GBP";
        long amount = 100;
        String user = "xfanth@gmail.com";
        bank.payUser(currency, amount, user);
    }
}
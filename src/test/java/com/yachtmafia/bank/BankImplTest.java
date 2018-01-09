package com.yachtmafia.bank;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by xfant on 2018-01-08.
 */
public class BankImplTest {
    BankImpl bank;

    @Test
    @Ignore
    public void test1() throws Exception {
        bank = new BankImpl();
        bank.test();
    }

    @Test
    public void payUser() throws Exception {
        bank = new BankImpl();
        String currency = "GBP";
        long amount = 100;
        String user = "PAMILA@gmail.com";
        bank.payUser(currency, amount, user);
    }
}
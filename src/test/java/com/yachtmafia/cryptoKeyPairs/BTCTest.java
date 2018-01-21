package com.yachtmafia.cryptoKeyPairs;

import org.junit.Before;
import org.junit.Test;

public class BTCTest {
    private static final int NUM_OF_TESTS = 1_000;
    private BTC btc;

    @Before
    public void setup(){
        btc = new BTC();
    }

    @Test
    public void getPublicAddress() {
        String publicAddress = btc.getPublicAddress();
        System.out.println(publicAddress);
    }

    @Test
    public void getPrivateAddress() {
        String privateAddress = btc.getPrivateKey();
        System.out.println(privateAddress);
    }

    @Test
    public void uniqueAddresses(){
        for (int i=0; i<NUM_OF_TESTS; i++){
            BTC btc1 = new BTC();
            assert !btc1.getPublicAddress().equals(btc.getPublicAddress());
            assert !btc1.getPrivateKey().equals(btc.getPrivateKey());
        }
    }
}
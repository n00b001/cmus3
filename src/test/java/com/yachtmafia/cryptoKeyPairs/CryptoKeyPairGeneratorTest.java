package com.yachtmafia.cryptoKeyPairs;

import org.bitcoinj.params.UnitTestParams;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by xfant on 2018-01-21.
 */
public class CryptoKeyPairGeneratorTest {
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void parse() throws Exception {
        CryptoKeyPair btc = CryptoKeyPairGenerator.parse("BTC", UnitTestParams.get());
        String privateKey = btc.getPrivateKey();
        String publicAddress = btc.getPublicAddress();
        assert privateKey != null;
        assert publicAddress != null;
    }

}
package com.yachtmafia.db;

import com.yachtmafia.cryptoKeyPairs.BTC;
import com.yachtmafia.messages.SwapMessage;
import org.bitcoinj.params.UnitTestParams;

import java.math.BigDecimal;

public class DBWrapperMock implements DBWrapper {
    private BTC btc = new BTC(UnitTestParams.get());

    @Override
    public String getPublicAddress(String user, String coin) {
        if ("BTC".equals(coin)){
            return btc.getPublicAddress();
        }
        return null;
    }

    @Override
    public boolean addNewWallet(String user, String coin, String publicAddress, String privateAddress) {
        return true;
    }

    @Override
    public boolean addPortfolioBalance(SwapMessage message, String purchasedAmount) {
        return true;
    }

    @Override
    public BigDecimal getFunds(String user, String coin) {
        return BigDecimal.valueOf(1d);
    }

    @Override
    public String getPrivateKey(String user, String coin) {
        if ("BTC".equals(coin)){
            return btc.getPrivateKey();
        }
        return null;
    }
}

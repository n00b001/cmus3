package com.yachtmafia.db;

import com.yachtmafia.messages.SwapMessage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class DBWrapperImpl implements DBWrapper {
    public DBWrapperImpl() {
        /**
         * todo: this whole class
         */
    }

    @Override
    public String getPublicAddress(String user, String coin) {
//        return null;
        throw new NotImplementedException();
    }

    @Override
    public boolean addNewWallet(String user, String coin, String publicAddress, String privateAddress) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addPortfolioBalance(SwapMessage message, long purchasedAmount) {
        throw new NotImplementedException();
    }

    @Override
    public double getFunds(String user, String coin) {
        throw new NotImplementedException();
    }

    @Override
    public String getPrivateKey(String user, String coin) {
        throw new NotImplementedException();
    }
}

package com.yachtmafia.walletwrapper;

/**
 * Created by xfant on 2018-01-08.
 */
public class WalletWrapperMock extends WalletWrapper {
    public WalletWrapperMock() {
        /**
         * Looks like nothing to me
         */
    }

    @Override
    public void startAsync() {
        /**
         * looks like nothing to me
         */
    }

    @Override
    public boolean sendTransaction(String privateKey, String publicAddress, String depositAddress, long amountOfCoin) {
        return true;
    }
}

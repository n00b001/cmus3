package com.yachtmafia.walletwrapper;

import com.yachtmafia.WalletAppKitMock;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.store.BlockStoreException;

/**
 * Created by xfant on 2018-01-08.
 */
public class WalletWrapperMock extends WalletWrapper {

    public WalletWrapperMock() throws BlockStoreException {
        super(new WalletAppKitMock(), new Web3jMock(new Web3jServiceMock(true)));
    }

    @Override
    public void startAsync() {
        /**
         * looks like nothing to me
         */
    }

    @Override
    public boolean sendBitcoinTransaction(String privateKey, String publicAddress, String depositAddress, String amountOfCoin, NetworkParameters network) {
        return true;
    }
}

package com.yachtmafia.walletwrapper;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;

/**
 * Created by xfant on 2018-01-08.
 */
public class WalletWrapperMock extends WalletWrapper {

    public WalletWrapperMock(WalletAppKit walletAppKit) {
        super(walletAppKit);
    }

    @Override
    public void startAsync() {
        /**
         * looks like nothing to me
         */
    }

    @Override
    public boolean sendTransaction(String privateKey, String publicAddress, String depositAddress, String amountOfCoin, NetworkParameters network) {
        return true;
    }
}

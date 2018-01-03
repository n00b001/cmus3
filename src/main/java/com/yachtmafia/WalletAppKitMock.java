package com.yachtmafia;

import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;

import java.io.File;

/**
 * Created by xfant on 2017-12-31.
 */
public class WalletAppKitMock extends WalletAppKit {
    public WalletAppKitMock() {
        super(MainNetParams.get(), new File("."), "");
    }

    @Override
    public PeerGroup peerGroup() {
        return new PeerGroup(MainNetParams.get());
    }
}

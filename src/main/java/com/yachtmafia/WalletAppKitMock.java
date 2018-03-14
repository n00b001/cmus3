package com.yachtmafia;

import org.bitcoinj.core.AbstractBlockChain;
import org.bitcoinj.core.Block;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.net.discovery.PeerDiscovery;
import org.bitcoinj.params.UnitTestParams;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.MemoryBlockStore;

import java.io.File;

/**
 * Created by xfant on 2017-12-31.
 */
public class WalletAppKitMock extends WalletAppKit {
    public WalletAppKitMock() throws BlockStoreException {
        super(UnitTestParams.get(), new File("wallet"), "forwarding-service-unittest");
//        startAsync();
//        awaitRunning();
//        chain().getChainHead().getHeader().solve();
//        connectToLocalHost();
//        BlockStore blockStore = new MemoryBlockStore(UnitTestParams.get());
//        AbstractBlockChain blockChain = new BlockChain(UnitTestParams.get(), blockStore);
//        PeerGroup peerGroup = new PeerGroup(UnitTestParams.get(), blockChain);
//        peerGroup.startAsync();
    }
}

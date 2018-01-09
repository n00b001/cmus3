package com.yachtmafia.walletWrapper;

import com.google.common.util.concurrent.ListenableFuture;
import org.apache.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xfant on 2018-01-07.
 */
public class WalletWrapper {
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private File file = new File(".");
    private WalletAppKit walletAppKit = new WalletAppKit(MainNetParams.get(), file, "");

    public WalletWrapper() {

    }

    public void startAsync() {
        walletAppKit.startAsync();
    }

    public boolean sendTransaction(String privateKey, String publicAddress, String depositAddress, long amountOfCoin) {
        try {
            ECKey ecKey = ECKey.fromPrivate(privateKey.getBytes());
            List<ECKey> ecKeyList = new ArrayList<>();
            ecKeyList.add(ecKey);
            Wallet wallet = Wallet.fromKeys(MainNetParams.get(), ecKeyList);

//            String depositAddress = exchange.getDepositAddress(swapMessage.getFromCoinName());
            Address address = Address.fromBase58(MainNetParams.get(), depositAddress);

            final Coin value = Coin.valueOf(amountOfCoin);
            // Make sure this code is run in a single thread at once.
            SendRequest request = SendRequest.to(address, value);
// The SendRequest object can be customized at this point to modify how the transaction will be created.
            wallet.completeTx(request);
// Ensure these funds won't be spent again.
            wallet.commitTx(request.tx);
//                wallet.saveToFile(...);
// A proposed transaction is now sitting in request.tx - send it in the background.
            ListenableFuture<Transaction> future = walletAppKit.peerGroup()
                    .broadcastTransaction(request.tx).future();

// The future will complete when we've seen the transaction ripple across the network to a sufficient degree.
// Here, we just wait for it to finish, but we can also attach a listener that'll get run on a background
// thread when finished. Or we could just assume the network accepts the transaction and carry on.
            Transaction transaction = future.get();
            LOG.info(transaction);
            return true;
        } catch (Exception e) {
            LOG.error("CAUGHT", e);
            return false;
        }
    }
}

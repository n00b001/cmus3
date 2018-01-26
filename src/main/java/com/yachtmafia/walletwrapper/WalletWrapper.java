package com.yachtmafia.walletwrapper;

import com.google.common.util.concurrent.ListenableFuture;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.yachtmafia.util.LoggerMaker.logError;
import static com.yachtmafia.util.LoggerMaker.logInfo;

/**
 * Created by xfant on 2018-01-07.
 */
public class WalletWrapper {
//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());

//    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private File file = new File(".");
    private WalletAppKit walletAppKit = new WalletAppKit(MainNetParams.get(), file, "");

    public void startAsync() {
        walletAppKit.startAsync();
    }

    public boolean sendTransaction(String privateKey, String publicAddress, String depositAddress, String amountOfCoin) {
        try {
            /**
             * todo: test
             */
            ECKey ecKey = ECKey.fromPrivate(privateKey.getBytes());
            List<ECKey> ecKeyList = new ArrayList<>();
            ecKeyList.add(ecKey);
            Wallet wallet = Wallet.fromKeys(MainNetParams.get(), ecKeyList);

            Address address = Address.fromBase58(MainNetParams.get(), depositAddress);

            Long satoshis = Long.valueOf(amountOfCoin);
            final Coin value = Coin.valueOf(satoshis);
            // Make sure this code is run in a single thread at once.
            SendRequest request = SendRequest.to(address, value);
// The SendRequest object can be customized at this point to modify how the transaction will be created.
            wallet.completeTx(request);
// Ensure these funds won't be spent again.
            wallet.commitTx(request.tx);
// A proposed transaction is now sitting in request.tx - send it in the background.
            ListenableFuture<Transaction> future = walletAppKit.peerGroup()
                    .broadcastTransaction(request.tx).future();

// The future will complete when we've seen the transaction ripple across the network to a sufficient degree.
// Here, we just wait for it to finish, but we can also attach a listener that'll get run on a background
// thread when finished. Or we could just assume the network accepts the transaction and carry on.
            Transaction transaction = future.get();
            logInfo(getClass(), "Transaction: " + transaction);
            return true;
        } catch (Exception e) {
            logError(getClass(), "CAUGHT", e);
            return false;
        }
    }
}

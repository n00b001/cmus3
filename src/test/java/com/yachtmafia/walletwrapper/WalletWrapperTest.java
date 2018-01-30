package com.yachtmafia.walletwrapper;

import com.yachtmafia.WalletAppKitMock;
import com.yachtmafia.cryptoKeyPairs.CryptoKeyPair;
import com.yachtmafia.cryptoKeyPairs.CryptoKeyPairGenerator;
import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.params.UnitTestParams;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.yachtmafia.util.Util.getUnitsPerCoin;

/**
 * Created by xfant on 2018-01-29.
 */
public class WalletWrapperTest {
    WalletWrapper walletWrapper;
    WalletAppKit walletAppKit = new WalletAppKitMock();

    @Before
    public void setUp() throws Exception {
        walletWrapper = new WalletWrapper(walletAppKit);
    }

    @Test
    public void startAsync() throws Exception {
        walletWrapper.startAsync();
    }

    @Test
    public void sendTransaction() throws Exception {
        walletAppKit.startAsync();
        NetworkParameters unitTestParams = walletAppKit.params();
        CryptoKeyPair btc = CryptoKeyPairGenerator.parse("BTC", unitTestParams);

        System.out.println(btc.getPublicAddress());

        Block genesisBlock = unitTestParams.getGenesisBlock();
        Block nextBlock = genesisBlock.createNextBlock(
                Address.fromBase58(unitTestParams, btc.getPublicAddress()));
        List<Transaction> transactionsNext = nextBlock.getTransactions();
        for (Transaction transaction : transactionsNext){
            List<TransactionOutput> outputs = transaction.getOutputs();
            for (TransactionOutput output : outputs) {
                System.out.println(output.toString() + "\n");
//                Address addressFromP2SH = output.getAddressFromP2SH(unitTestParams);
//                System.out.println(addressFromP2SH);
            }
        }

        Wallet wallet = new Wallet(unitTestParams);
        wallet.importKey(ECKey.fromPrivate(btc.getPrivateKey().getBytes()));

        CryptoKeyPair depositAddress = CryptoKeyPairGenerator.parse("BTC", unitTestParams);

        String privateKey = btc.getPrivateKey();
        String publicAddress = btc.getPublicAddress();
        String despositAddress = depositAddress.getPublicAddress();
        String amountOfCoin = getUnitsPerCoin("BTC").toPlainString();
        walletWrapper.sendTransaction(privateKey, publicAddress,
                despositAddress, amountOfCoin, unitTestParams);
    }

}
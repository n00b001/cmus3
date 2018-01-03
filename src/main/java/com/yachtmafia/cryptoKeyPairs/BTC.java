package com.yachtmafia.cryptoKeyPairs;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;

public class BTC implements CryptoKeyPair {
    private final String privateKeyStr;
    private final String publicAddress;

    public BTC() {
        ECKey ecKey = new ECKey();
//        privateKeyStr = ecKey.getPrivateKeyAsHex();
//        StringBuilder builder = new StringBuilder();
//        ecKey.formatKeyWithAddress(true, builder, MainNetParams.get());
        privateKeyStr = ecKey.getPrivateKeyAsWiF(MainNetParams.get());
//        String privateKey3 = ecKey.getEncryptedPrivateKey().toString();
//        String privateKey4 = ecKey.getPrivKey().toString();
//        String privateKey5 = new String(ecKey.getPrivKeyBytes());
//        String privateKey6 = ecKey.getPrivateKeyEncoded(MainNetParams.get()).toString();
//         privateKeyStr = ecKey.getPrivateKeyEncoded(MainNetParams.get()).toBase58();
//        String privateKey8 = ecKey.getPrivateKeyEncoded(MainNetParams.get()).getKey().toString();


        publicAddress = ecKey.toAddress(MainNetParams.get()).toBase58();
//        System.out.println("");
    }

    @Override
    public String getPublicAddress() {
        return publicAddress;
    }

    @Override
    public String getPrivateKey() {
        return privateKeyStr;
    }
}

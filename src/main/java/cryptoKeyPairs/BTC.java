package cryptoKeyPairs;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.params.MainNetParams;

public class BTC implements CryptoKeyPair {
    private final String privateKeyStr;
    private final String publicAddress;

    public BTC() {
        ECKey ecKey = new ECKey();
        privateKeyStr = ecKey.getPrivateKeyAsWiF(MainNetParams.get());
        publicAddress = ecKey.toAddress(MainNetParams.get()).toBase58();
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

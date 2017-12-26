package cryptoKeyPairs;

public class CryptoKeyPairGetter {
    public static CryptoKeyPair parse(String toCoinName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> coinClass = Class.forName(toCoinName);
        return (CryptoKeyPair) coinClass.newInstance();
    }
}

package cryptoKeyPairs;

public class CryptoKeyPairGenerator {
    private static final String BASE_NAME = "cryptoKeyPairs.";
    public static CryptoKeyPair parse(String toCoinName) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> coinClass = Class.forName(BASE_NAME + toCoinName);
        return (CryptoKeyPair) coinClass.newInstance();
    }
}

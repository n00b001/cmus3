package db;

import cryptoKeyPairs.BTC;
import messages.SwapMessage;

public class DBWrapperMock implements DBWrapper {
    BTC btc = new BTC();

    @Override
    public String getPublicAddress(String user, String coin) {
        if ("BTC".equals(coin)){
            return btc.getPublicAddress();
        }
        return null;
    }

    @Override
    public boolean addNewWallet(String user, String coin, String publicAddress, String privateAddress) {
        return true;
    }

    @Override
    public boolean addPortfolioBalance(SwapMessage message, long purchasedAmount) {
        return true;
    }

    @Override
    public double getFunds(String user, String coin) {
        return 1;
    }

    @Override
    public String getPrivateKey(String user, String coin) {
        if ("BTC".equals(coin)){
            return btc.getPrivateKey();
        }
        return null;
    }
}

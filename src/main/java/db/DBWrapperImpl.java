package db;

import messages.SwapMessage;

public class DBWrapperImpl implements DBWrapper {
    public DBWrapperImpl() {

    }

    @Override
    public String getPublicAddress(String user, String coin) {
        return null;
    }

    @Override
    public boolean addNewWallet(String user, String coin, String publicAddress, String privateAddress) {
        return false;
    }

    @Override
    public boolean addPortfolioBalance(SwapMessage message, double exchangeRate) {
        return false;
    }

    @Override
    public double getFunds(String user, String coin) {
        return 0;
    }

    @Override
    public String getPrivateKey(String user, String coin) {
        return null;
    }
}

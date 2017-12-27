package db;

import messages.SwapMessage;

public class DBWrapperMock implements DBWrapper {
    @Override
    public String getPublicAddress(String user, String coin) {
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
        return 0;
    }

    @Override
    public String getPrivateKey(String user, String coin) {
        return null;
    }
}

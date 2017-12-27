package db;

import messages.SwapMessage;

public interface DBWrapper {
    String getPublicAddress(String user, String coin);
    boolean addNewWallet(String user, String coin, String publicAddress, String privateAddress);
    boolean addPortfolioBalance(SwapMessage message, long purchasedAmount);
    double getFunds(String user, String coin);
    String getPrivateKey(String user, String coin);
}

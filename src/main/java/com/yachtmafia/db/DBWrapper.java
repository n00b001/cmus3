package com.yachtmafia.db;

import com.yachtmafia.messages.SwapMessage;

import java.sql.SQLException;

public interface DBWrapper {
    String getPublicAddress(String user, String coin) throws SQLException;
    boolean addNewWallet(String user, String coin, String publicAddress, String privateAddress);
    boolean addPortfolioBalance(SwapMessage message, long purchasedAmount);
    double getFunds(String user, String coin);
    String getPrivateKey(String user, String coin);
}

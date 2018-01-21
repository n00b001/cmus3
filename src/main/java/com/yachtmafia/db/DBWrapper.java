package com.yachtmafia.db;

import com.yachtmafia.messages.SwapMessage;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface DBWrapper {
    String getPublicAddress(String user, String coin) throws SQLException;
    boolean addNewWallet(String user, String coin, String publicAddress, String privateAddress);
    boolean addPortfolioBalance(SwapMessage message, String purchasedAmount);
    BigDecimal getFunds(String user, String coin);
    String getPrivateKey(String user, String coin);

}

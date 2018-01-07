package com.yachtmafia.db;

import com.yachtmafia.config.Config;
import com.yachtmafia.messages.SwapMessage;
import org.apache.log4j.Logger;

import java.sql.*;


public class DBWrapperImpl implements DBWrapper {
    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private Config config;
    public DBWrapperImpl(Config config) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Connector not set up!", e);
        }
        this.config = config;
    }


    @Override
    public boolean addNewWallet(String user, String coin, String publicAddress, String privateAddress) {
        String query =
                "SELECT " +
                        "    currencyId " +
                        "FROM " +
                        "    currencies " +
                        "WHERE " +
                        "    symbol = '" + coin + "'";
        String currencyID = getSingleQueryString(query);

        query =
                "SELECT " +
                        "    userId " +
                        "FROM " +
                        "    users " +
                        "WHERE " +
                        "    email = '" + user + "'";
        String userId = getSingleQueryString(query);

//        String amount = String.valueOf(message.getAmountOfCoin());


//        query =
//                "SELECT " +
//                "    SUM(amount) " +
//                "FROM "+
//                "    portfoliobalances " +
//                "WHERE "+
//                "    userId = '" + userId + "' AND currencyId = '" + currencyID + "'";
//        String runningTotal = getSingleQueryString(query);

//        String leftSide = getSingleQueryString(query);
//        String rightSide = getSingleQueryString(query);

        query =
                "INSERT INTO " +
                        "    wallets " +
                        "    (currencyId, userId, publickey) " +
                        "VALUES " +
                        "    ('" + currencyID + "', '" + userId + "', '" + publicAddress + "') ";
        try(Connection con= DriverManager.getConnection(
                config.connectionString,
                config.username, config.password);
            Statement stmt = con.createStatement()
        ){
            int rs = stmt.executeUpdate(query);
            LOG.info(rs);
//            rs.close();
        } catch (SQLException e) {
            LOG.error("Caught: ", e);
            return false;
        }

        String walletId = "";

        query =
                "INSERT INTO " +
                        "    privatekeys " +
                        "    (walletId, privKey) " +
                        "VALUES " +
                        "    ('" + walletId + "', '" + privateAddress + "') ";
        try (Connection con = DriverManager.getConnection(
                config.connectionString,
                config.username, config.password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)
        ) {
            rs.close();
        } catch (SQLException e) {
            LOG.error("Caught: ", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean addPortfolioBalance(SwapMessage message, long purchasedAmount) {
        boolean success = true;
        String query =
                "SELECT " +
                        "    currencyId " +
                        "FROM " +
                        "    currencies " +
                        "WHERE " +
                        "    symbol = '" + message.getFromCoinName() + "'";
        String currencyID = getSingleQueryString(query);

        query =
                "SELECT " +
                        "    userId " +
                        "FROM " +
                        "    users " +
                        "WHERE " +
                        "    email = '" + message.getUsername() + "'";
        String userId = getSingleQueryString(query);

        String amount = String.valueOf(message.getAmountOfCoin());

        query =
                "SELECT " +
                        "    SUM(amount) " +
                        "FROM " +
                        "    portfoliobalances " +
                        "WHERE " +
                        "    userId = '" + userId + "' AND currencyId = '" + currencyID + "'";
        String runningTotal = getSingleQueryString(query);

//        String leftSide = getSingleQueryString(query);
//        String rightSide = getSingleQueryString(query);

        query =
                "INSERT INTO" +
                        "    portfoliobalances" +
                        "    (currencyId, userId, amount, runningTotal)" +
                        "VALUES" +
                        "    ('" + currencyID + "', '" + userId + "', '" + amount + "', '" + runningTotal + "')";
        try (Connection con = DriverManager.getConnection(
                config.connectionString,
                config.username, config.password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)
        ) {
            rs.close();
        } catch (SQLException e) {
            LOG.error("Caught: ", e);
            success = false;
        }
        return success;
    }

    @Override
    public double getFunds(String user, String coin) {
        String query =
                "SELECT " +
                        "   runningTotal " +
                        "FROM " +
                        "    portfoliobalances P INNER JOIN users U ON P.userID = U.userID " +
                        "    INNER JOIN currencies C ON C.currencyID = P.currencyID " +
                        "WHERE " +
                        "    U.email = '" + user + "' AND C.symbol = '" + coin + "'";
        String funds = getSingleQueryString(query);
        if (funds != null) {
            return Double.parseDouble(funds);
        } else {
            return 0;
        }
    }

    @Override
    public String getPrivateKey(String user, String coin) {
        String query =
                "SELECT " +
                        "   privKey " +
                        "FROM " +
                        "    privatekeys P " +
                        "    INNER JOIN wallets W ON P.walletId = W.walletId " +
                        "    INNER JOIN users U ON W.userID = U.userID " +
                        "WHERE " +
                        "    U.email = '" + user + "' AND C.symbol = '" + coin + "'";
        return getSingleQueryString(query);
    }

    @Override
    public String getPublicAddress(String user, String coin) {
        String query =
                "SELECT " +
                        "   publickey " +
                        "FROM " +
                        "    wallets W INNER JOIN users U ON W.userID = U.userID " +
                        "    INNER JOIN currencies C ON C.currencyID = W.currencyID " +
                        "WHERE " +
                        "    U.email = '" + user + "' AND C.symbol = '" + coin + "'";
        return getSingleQueryString(query);
    }

    private String getSingleQueryString(String query) {
        try (Connection con = DriverManager.getConnection(
                config.connectionString,
                config.username, config.password);
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)
        ) {
            if (rs.next()) {
                String string = rs.getString(1);
                if (rs.next()) {
                    LOG.error("More than one element was found for query: " + query);
                }
                return string;
            } else {
                return null;
            }
        } catch (SQLException e) {
            LOG.error("Caught: ", e);
        }
        return null;
    }
}

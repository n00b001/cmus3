package com.yachtmafia.db;

import com.yachtmafia.config.Config;
import com.yachtmafia.messages.SwapMessage;
import org.apache.log4j.Logger;

import java.sql.*;

import static com.yachtmafia.util.Util.getCoinDoubleValue;


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
                        "    "+config.ID +" " +
                        " FROM " +
                        config.CURRENCIES_TABLE +
                        " WHERE " +
                        "    symbol = '" + coin + "'";
        String currencyID = getSingleQueryString(query);
        if (currencyID == null) {
            LOG.error("Currency not found: " + coin);
            return false;
        }

        query =
                "SELECT " +
                        "    userId " +
                        "FROM " +
                        config.USERS_TABLE +
                        " WHERE " +
                        "    email = '" + user + "'";
        String userId = getSingleQueryString(query);
        if (userId == null) {
            LOG.error("User not found: " + user);
            return false;
        }

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
                        config.WALLETS_TABLE +
                        "    (currencyId, userId, publicaddress) " +
                        " VALUES " +
                        "    ('" + currencyID + "', '" + userId + "', '" + publicAddress + "') ";

        String walletId;

        try(Connection con= DriverManager.getConnection(
                config.connectionString,
                config.username, config.password);
            Statement stmt = con.createStatement()
        ){
            int rs = stmt.executeUpdate(query);
            walletId = String.valueOf(rs);
//            LOG.info(rs);
//            rs.close();
        } catch (SQLException e) {
            LOG.error("Caught: ", e);
            return false;
        }

        query =
                "INSERT INTO " +
                        config.PRIVATE_TABLE +
                        "    (walletId, privKey) " +
                        " VALUES " +
                        "    ('" + walletId + "', '" + privateAddress + "') ";
        try (Connection con = DriverManager.getConnection(
                config.connectionString,
                config.username, config.password);
             Statement stmt = con.createStatement()
        ) {
            int rs = stmt.executeUpdate(query);
            LOG.debug(rs);
        } catch (SQLException e) {
            LOG.error("Caught: ", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean addPortfolioBalance(SwapMessage message, String toAmount) {
        boolean success = true;
        String fromAmount = message.getAmountOfCoin();

        String currencyIDfrom = getCurrencyId(message.getFromCoinName());
        String currencyIDto = getCurrencyId(message.getFromCoinName());

        String query =
                "SELECT " +
                        "    "+config.ID+" " +
                        " FROM " +
                        config.USERS_TABLE +
                        " WHERE " +
                        "    email = '" + message.getUsername() + "'";
        String userId = getSingleQueryString(query);
        if (userId == null){
            throw new RuntimeException("userId not found: " + message.getUsername());
        }

        double exchangeRate = getCoinDoubleValue(toAmount, message.getToCoinName())/
                getCoinDoubleValue(fromAmount, message.getFromCoinName());

        query = "INSERT INTO " +
                        config.PORTFOLIO_TABLE +
                        "    (from_currency_id, user_id, from_amount, to_amount, to_currency_id, exchange_rate)" +
                        " VALUES" +
                        "    ('" + currencyIDfrom + "', '" + userId + "', '" + fromAmount + "', '"
                        + toAmount + "', '" + currencyIDto + "', '" + exchangeRate + "')";

        try (Connection con = DriverManager.getConnection(
                config.connectionString,
                config.username, config.password);
             Statement stmt = con.createStatement()
        ) {
            int rs = stmt.executeUpdate(query);
        } catch (SQLException e) {
            LOG.error("Caught: ", e);
            success = false;
        }
        return success;
    }

    private String getCurrencyId(String coinName) {
        String query =
                "SELECT " +
                        "    "+config.ID+" " +
                        " FROM " +
                        config.CURRENCIES_TABLE +
                        " WHERE " +
                        "    symbol = '" + coinName + "'";
        String currencyID = getSingleQueryString(query);
        if (currencyID == null){
            throw new RuntimeException("Currency not found: " + coinName);
        }
        return currencyID;
    }


    @Override
    public Double getFunds(String user, String coin) {
        String query =
                "SELECT " +
                        "   runningTotal " +
                        " FROM " +
                        "    " + config.PORTFOLIO_TABLE + " P INNER JOIN " + config.USERS_TABLE
                        + " U ON P.userID = U.userID " +
                        "    INNER JOIN " + config.CURRENCIES_TABLE + " C ON C.currencyID = P.currencyID " +
                        " WHERE " +
                        "    U.email = '" + user + "' AND C.symbol = '" + coin + "'";
        String funds = getSingleQueryString(query);
        if (funds != null) {
            return Double.parseDouble(funds);
        }
        return null;
    }

    @Override
    public String getPrivateKey(String user, String coin) {
        String query =
                "SELECT " +
                        "   "+config.PRIVATE_KEY+" " +
                        " FROM " +
                        "    " + config.PRIVATE_TABLE + " P" +
                        "    INNER JOIN " + config.WALLETS_TABLE+" W ON P.walletId = W.walletId " +
                        "    INNER JOIN " + config.USERS_TABLE + " U ON W.userID = U.userID " +
                        "    INNER JOIN " + config.CURRENCIES_TABLE + " C ON C.symbol = '" + coin + "'" +
                        " WHERE " +
                        "    U.email = '" + user + "' AND C.symbol = '" + coin + "'";
        return getSingleQueryString(query);
    }

    @Override
    public String getPublicAddress(String user, String coin) {
        String query =
                "SELECT " +
                        "   "+config.PUBLIC_ADDRESS+" " +
                        " FROM " +
                        "    " + config.WALLETS_TABLE + " W INNER JOIN " + config.USERS_TABLE
                        + " U ON W."+config.USER_ID+" = U."+config.ID+" " +
                        "    INNER JOIN " + config.CURRENCIES_TABLE + " C " +
                        " ON C."+config.CURRENCY_ID+" = W."+config.CURRENCY_ID+" " +
                        " WHERE " +
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

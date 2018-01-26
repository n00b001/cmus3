package com.yachtmafia.db;

import com.yachtmafia.config.Config;
import com.yachtmafia.messages.SwapMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;

import static com.yachtmafia.util.LoggerMaker.logError;
import static com.yachtmafia.util.LoggerMaker.logInfo;
import static com.yachtmafia.util.LoggerMaker.logWarning;
import static com.yachtmafia.util.Util.getCoinDoubleValue;


public class DBWrapperImpl implements DBWrapper {
//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());

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
                        "    "+config.SYMBOL+" = '" + coin + "'";
        String currencyID = getSingleQueryString(query);
        if (currencyID == null) {
            logError(getClass(), "Currency not found: " + coin);
            return false;
        }

        query =
                "SELECT " +
                        "    "+config.ID+" " +
                        "FROM " +
                        config.USERS_TABLE +
                        " WHERE " +
                        "    "+config.EMAIL+" = '" + user + "'";
        String userId = getSingleQueryString(query);
        if (userId == null) {
            logError(getClass(), "User not found: " + user);
            return false;
        }

        query =
                "SELECT " + config.ID + " FROM " +
                        config.WALLETS_TABLE + " WHERE " + config.CURRENCY_ID
                        + " = " + currencyID + " AND " + config.USER_ID + " = " + userId
                        + " AND " + config.PUBLIC_ADDRESS + " is not null";
        String userWallet = getSingleQueryString(query);

        if (userWallet != null) {
            query =
                    "SELECT "+config.ID+" FROM " +
                            config.PRIVATE_TABLE + " WHERE " + config.WALLET_ID
                            + " = " + userWallet + " AND " + config.PRIVATE_KEY + " is not null";
            String userPrivatekey = getSingleQueryString(query);
            if (userPrivatekey != null){
                logInfo(getClass(), "User already has wallet!");
                return true;
            }else{
                /**
                 * create private key
                 */
                return insertPrivateKey(privateAddress, userWallet);
            }
        }else{
            /**
             * create wallet and private key
             */
            query =
                    "INSERT INTO " +
                            config.WALLETS_TABLE +
                            "    ("+config.CURRENCY_ID+", "+config.USER_ID+", "+config.PUBLIC_ADDRESS+") " +
                            " VALUES " +
                            "    ('" + currencyID + "', '" + userId + "', '" + publicAddress + "') ";

            String walletId;

            try(Connection con= DriverManager.getConnection(
                    config.connectionString,
                    config.username, config.password);
                Statement stmt = con.createStatement()
            ){
                int rs = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                try(ResultSet generatedKeys = stmt.getGeneratedKeys();) {
                    if (generatedKeys.next()) {
                        walletId = String.valueOf(generatedKeys.getLong(1));
                    }
                    else {
                        logError(getClass(), "Failed to add wallet");
                        return false;
                    }
                }
            } catch (SQLException e) {
                logError(getClass(), "Caught: ", e);
                return false;
            }

            return insertPrivateKey(privateAddress, walletId);
        }
    }

    private boolean insertPrivateKey(String privateAddress, String userWallet) {
        String query;
        query =
                "INSERT INTO " +
                        config.PRIVATE_TABLE +
                        "    ("+config.WALLET_ID+", "+config.PRIVATE_KEY+") " +
                        " VALUES " +
                        "    ('" + userWallet + "', '" + privateAddress + "') ";
        try (Connection con = DriverManager.getConnection(
                config.connectionString,
                config.username, config.password);
             Statement stmt = con.createStatement()
        ) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            logError(getClass(), "Caught: ", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean addPortfolioBalance(SwapMessage message, String toAmount) {
        boolean success = true;
        String fromAmount = message.getAmountOfCoin();
        if (null == fromAmount || "".equals(fromAmount) || "0".equals(fromAmount)){
            throw new RuntimeException("Transaction from amount is invalid: " + fromAmount);
        }
        if (null == toAmount || "".equals(toAmount) || "0".equals(toAmount)){
            throw new RuntimeException("Transaction to amount is invalid: " + toAmount);
        }

        String currencyIDfrom = getCurrencyId(message.getFromCoinName());
        String currencyIDto = getCurrencyId(message.getToCoinName());

        String query =
                "SELECT " +
                        "    "+config.ID+" " +
                        " FROM " +
                        config.USERS_TABLE +
                        " WHERE " +
                        "    "+config.EMAIL+" = '" + message.getUsername() + "'";
        String userId = getSingleQueryString(query);
        if (userId == null){
            throw new RuntimeException("userId not found: " + message.getUsername());
        }

        RoundingMode roundingMode = RoundingMode.HALF_EVEN;
        BigDecimal coinToDoubleValue = getCoinDoubleValue(toAmount, message.getToCoinName());
        BigDecimal coinFromDoubleValue = getCoinDoubleValue(fromAmount, message.getFromCoinName());

        BigDecimal exchangeRate = coinToDoubleValue.divide(coinFromDoubleValue, roundingMode);

        query = "INSERT INTO " +
                        config.PORTFOLIO_TABLE +
                        "    (from_currency_id, user_id, from_amount, to_amount, to_currency_id, exchange_rate)" +
                        " VALUES" +
                        "    ('" + currencyIDfrom + "', '" + userId + "', '" + fromAmount + "', '"
                        + toAmount + "', '" + currencyIDto + "', '" + exchangeRate.doubleValue() + "')";

        try (Connection con = DriverManager.getConnection(
                config.connectionString,
                config.username, config.password);
             Statement stmt = con.createStatement()
        ) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            logError(getClass(), "Caught: ", e);
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
                        "    "+config.SYMBOL+" = '" + coinName + "'";
        String currencyID = getSingleQueryString(query);
        if (currencyID == null){
            throw new RuntimeException("Currency not found: " + coinName);
        }
        return currencyID;
    }


    @Override
    public BigDecimal getFunds(String user, String coin) {
        String query =
                "SELECT " +
                        "   SUM(to_amount) " +
                        " FROM " +
                        "    " + config.PORTFOLIO_TABLE + " P INNER JOIN " + config.USERS_TABLE
                        + " U ON P."+ config.USER_ID+" = U."+config.ID + " " +
                        "    INNER JOIN " + config.CURRENCIES_TABLE + " C ON C."+config.ID+" = P."+config.TO_CURRENCY_ID+" " +
                        " WHERE " +
                        "    U."+config.EMAIL+" = '" + user + "' AND C."+config.SYMBOL+" = '" + coin + "'";
        String toFunds = getSingleQueryString(query);

        if (toFunds == null){
            logWarning(getClass(), String.format("No to funds found for user: %s and coin: %s", user, coin));
            return null;
        }

        query =
                "SELECT " +
                        "   SUM(from_amount) " +
                        " FROM " +
                        "    " + config.PORTFOLIO_TABLE + " P INNER JOIN " + config.USERS_TABLE
                        + " U ON P."+ config.USER_ID+" = U."+config.ID + " " +
                        "    INNER JOIN " + config.CURRENCIES_TABLE + " C ON C."+config.ID+" = P."+config.FROM_CURRENCY_ID+" " +
                        " WHERE " +
                        "    U."+config.EMAIL+" = '" + user + "' AND C."+config.SYMBOL+" = '" + coin + "'";
        String fromFunds = getSingleQueryString(query);

        if (fromFunds == null){
            logWarning(getClass(), String.format("No from funds found for user: %s and coin: %s", user, coin));
            return null;
        }

        BigDecimal toFundsDec = BigDecimal.valueOf(Long.parseLong(toFunds));
        BigDecimal fromFundsDec = BigDecimal.valueOf(Long.parseLong(fromFunds));

        return toFundsDec.subtract(fromFundsDec);
    }

    @Override
    public String getPrivateKey(String user, String coin) {
        String query =
                "SELECT " +
                        "   "+config.PRIVATE_KEY+" " +
                        " FROM " +
                        "    " + config.PRIVATE_TABLE + " P" +
                        "    INNER JOIN " + config.WALLETS_TABLE+" W ON P."+config.WALLET_ID+" = W."+config.ID+" " +
                        "    INNER JOIN " + config.USERS_TABLE + " U ON W."+config.USER_ID+" = U."+config.ID+" " +
                        "    INNER JOIN " + config.CURRENCIES_TABLE + " C ON C."+config.SYMBOL+" = '" + coin + "'" +
                        " WHERE " +
                        "    U."+config.EMAIL+" = '" + user + "' AND C."+config.SYMBOL+" = '" + coin + "'";
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
                        " ON C."+config.ID+" = W."+config.CURRENCY_ID+" " +
                        " WHERE " +
                        "    U."+config.EMAIL+" = '" + user + "' AND C."+config.SYMBOL+" = '" + coin + "'";
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
                    logError(getClass(), "More than one element was found for query: " + query);
                }
                return string;
            } else {
                return null;
            }
        } catch (SQLException e) {
            logError(getClass(), "Caught: ", e);
        }
        return null;
    }
}

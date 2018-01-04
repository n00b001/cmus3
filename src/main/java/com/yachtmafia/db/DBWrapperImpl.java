package com.yachtmafia.db;

import com.yachtmafia.config.Config;
import com.yachtmafia.messages.SwapMessage;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    public String getPublicAddress(String user, String coin) {
        String query =
                "SELECT " +
                "   publickey " +
                "FROM " +
                "    wallets W INNER JOIN users U ON W.userID = U.userID " +
                "    INNER JOIN currencies C ON C.currencyID = W.currencyID " +
                "WHERE " +
                "    U.email = @email AND C.symbol = @symbol";
        try(Connection con= DriverManager.getConnection(
                config.connectionString,
                config.username, config.password);
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery(query);
        ){
            if (rs.wasNull()){
                return null;
            }
            String string = rs.getString(1);
//            while(rs.next()){
//            }
        } catch (SQLException e) {
            LOG.error("Caught: ", e);
            return null;
        }
//        return null;
    }

    @Override
    public boolean addNewWallet(String user, String coin, String publicAddress, String privateAddress) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addPortfolioBalance(SwapMessage message, long purchasedAmount) {
        throw new NotImplementedException();
    }

    @Override
    public double getFunds(String user, String coin) {
        throw new NotImplementedException();
    }

    @Override
    public String getPrivateKey(String user, String coin) {
        throw new NotImplementedException();
    }
}

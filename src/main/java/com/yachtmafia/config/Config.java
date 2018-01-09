package com.yachtmafia.config;

/**
 * Created by xfant on 2018-01-04.
 */
public class Config {
    //    public String database_name = "heroku_8fe142771c0a565";
//    public String port = "3306";
//    public String hostname = "eu-cdbr-west-01.cleardb.com";
//    public String connectionString = "jdbc:mysql://" + hostname + ":" + port + "/" + database_name;
//    public String username = "b9fa8d64d98abc";
//    public String password = "e86b2e53";
    public String database_name = "cryptobox-dev";
    public String port = "3306";
    public String hostname = "35.197.215.225";
    public String connectionString = "jdbc:mysql://" + hostname + ":" + port + "/" + database_name;
    public String username = "root";
    public String password = "Watersports2017";

    public String CURRENCIES_TABLE = "currencies";
    public String USERS_TABLE = "users";
    public String WALLETS_TABLE = "wallets";
    public String PRIVATE_TABLE = "privatekeys";
    public String PORTFOLIO_TABLE = "portfoliobalances";

    public String CLIENT_ID_PAYPAL = "AbTewYvrX2Ts8bDNai80TeybnI8G9qKoPsUQoZN8Qs0fMvZZJsgCRBeRyIduGsuYLZ-sbgj47ZNeNFeV";
    public String CLIENT_SECRET_PAYPAL = "EBT3S1kHYZVH6Um_VlIfRRTEKTc86WzUniKvac770EL6J1T2ig77X2VrLWpfx4tBBBNwkQoCGXFhpjcK";

    public String COINBASE_KEY = "k3d1JeBuZoqPXDNM";
    public String COINBASE_SECRET = "yoSgwEVgXVRRnfeW8OZe8QybYZXDg9RJ";

}

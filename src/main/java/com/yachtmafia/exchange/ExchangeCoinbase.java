package com.yachtmafia.exchange;

import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.coinbase.api.entity.Transfer;
import com.coinbase.api.exception.CoinbaseException;
import com.yachtmafia.config.Config;
//import org.apache.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by xfant on 2018-01-20.
 */
public class ExchangeCoinbase implements Exchange {
    private final Coinbase cb;
    private final Config config;
//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());
    private static final Logger logger = LogManager.getLogger(Exchange.class);


    public ExchangeCoinbase(Config config) {
        this.config = config;
//        cb = new CoinbaseBuilder()
//                .withApiKey(config.COINBASE_KEY, config.COINBASE_SECRET)
//                .build();

        cb = new CoinbaseBuilder()
                .withAccessToken(config.COINBASE_ACCESS_TOKEN_LIVE)
                .build();
    }

    @Override
    public String exchangeCurrency(String from, String to, String amount) {
        try {
            if ("BTC".equals(to) && "GBP".equals(from)) {
                Money money = Money.ofMinor(CurrencyUnit.getInstance(from), Long.valueOf(amount));
                Transfer buy = cb.buy(money);
                handleStatus(buy);
            } else if ("GBP".equals(to) && "BTC".equals(from)){
                Money money = Money.ofMinor(CurrencyUnit.getInstance(from), Long.valueOf(amount));
                Transfer buy = cb.sell(money);
                handleStatus(buy);
            }
//            else{
//                throw new RuntimeException("Un supported pair! to: "
//                        + to + " from: " + from + " amount: " + amount);

//            }
        }catch (IOException|CoinbaseException|InterruptedException e){
            logger.warn("Caught: ", e);

        }
//        throw new NotImplementedException();
        return null;
    }

    private void handleStatus(Transfer buy) throws InterruptedException {
        Transfer.Status status = buy.getStatus();
        while(status.equals(Transfer.Status.CREATED)){
            logger.info("Transaction created...");
            Thread.sleep(1000);
            status = buy.getStatus();
        }
        while(status.equals(Transfer.Status.PENDING)){
            logger.info("Transaction pending...");
            Thread.sleep(1000);
            status = buy.getStatus();
        }
        if(Transfer.Status.COMPLETE.equals(status)){
            logger.info("Success!");
        }else{
            logger.error("Failure!");
        }
    }

    @Override
    public boolean withdrawCrypto(String coinName, String address, String amount) {
        throw new NotImplementedException();
    }

    @Override
    public String getDepositAddress(String fromCoinName) {
        throw new NotImplementedException();
    }

    @Override
    public boolean withdrawToBank(String toCoinName, String purchasedAmount) {
        throw new NotImplementedException();
    }

    @Override
    public Set<String> getAvailableCoins() {
        Set<String> returnSet = new HashSet<>();
        try {
            cb.getSupportedCurrencies().forEach(
                    a -> returnSet.add(a.getSymbol()));
        } catch (CoinbaseException| IOException e) {
            logger.error("Caught: ", e);
        }
        return returnSet;
    }

    @Override
    public String getLowestPrice(String symbolPair) {
        if (symbolPair == null){
            logger.error("Symbol pair == null");
        }else if (!symbolPair.contains("BTC")){
            logger.error("Currency not supported: " + symbolPair);
        }else{
            try {
                String currency = symbolPair.replace("BTC", "");
                Money spotPrice = cb.getSpotPrice(CurrencyUnit.getInstance(currency));
                return spotPrice.getAmount().toPlainString();
            }catch (IOException| CoinbaseException ex){
                logger.error("Caught: ", ex);
            }
        }
        return null;
//
//        try {
//            switch (symbolPair) {
//                case "BTCGBP":
//                case "GBPBTC": {
//                    Money spotPrice = cb.getSpotPrice(CurrencyUnit.GBP);
//                    return spotPrice.getAmount().toPlainString();
//                }
////                    break;
//                case "BTCEUR":
//                case "EURBTC": {
//                    Money spotPrice = cb.getSpotPrice(CurrencyUnit.EUR);
//                    return spotPrice.getAmount().toPlainString();
//                }
//                case "BTCUSD":
//                case "USDBTC": {
//                    Money spotPrice = cb.getSpotPrice(CurrencyUnit.USD);
//                    return spotPrice.getAmount().toPlainString();
//                }
//                default:
//                    logWarning(getClass(), "Symbol pair not supported: " + symbolPair);
//            }
//        }catch (IOException| CoinbaseException ex){
//            logError(getClass(), "Caught error: ", ex);
//        }
//        return null;
//        String returnString = "";
//        throw new NotImplementedException();
//        try {
////            cb.getOrders()
//        } catch (CoinbaseException| IOException e) {
//            LOG.error("Caught: " + e);
//        }
//        return returnString;
    }

    @Override
    public String getHighestPrice(String symbolPair) {
        if (symbolPair == null){
            logger.error("Symbol pair == null");

        }else if (!symbolPair.contains("BTC")){
            logger.error("Currency not supported: " + symbolPair);
        }else{
            try {
                String currency = symbolPair.replace("BTC", "");
                Money spotPrice = cb.getSpotPrice(CurrencyUnit.getInstance(currency));
                return spotPrice.getAmount().toPlainString();
            }catch (IOException| CoinbaseException ex){
                logger.error("Caught: ", ex);
            }
        }
        return null;
    }
}

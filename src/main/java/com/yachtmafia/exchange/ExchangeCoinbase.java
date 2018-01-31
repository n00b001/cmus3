package com.yachtmafia.exchange;

import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.coinbase.api.exception.CoinbaseException;
import com.yachtmafia.config.Config;
//import org.apache.log4j.Logger;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static com.yachtmafia.util.LoggerMaker.logError;
import static com.yachtmafia.util.LoggerMaker.logWarning;

/**
 * Created by xfant on 2018-01-20.
 */
public class ExchangeCoinbase implements Exchange {
    private Coinbase cb;
    private Config config;
//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());


    public ExchangeCoinbase(Config config) {
        this.config = config;
//        cb = new CoinbaseBuilder()
//                .withApiKey(config.COINBASE_KEY, config.COINBASE_SECRET)
//                .build();

        cb = new CoinbaseBuilder()
                .withAccessToken("4a9cb0ba78434e59c5371fb388f928db72e8cd407dd2f6590f6a47a3a672cd39")
                .build();
    }

    @Override
    public String exchangeCurrency(String from, String to, String amount) {
        throw new NotImplementedException();
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
            logError(getClass(), "Caught: " + e);
        }
        return returnSet;
    }

    @Override
    public String getLowestPrice(String symbolPair) {
        if (symbolPair == null){
            logError(getClass(), "symbolPair == null ");
        }else if (!symbolPair.contains("BTC")){
            logError(getClass(), "Currency not supported: " + symbolPair);
        }else{
            try {
                String currency = symbolPair.replace("BTC", "");
                Money spotPrice = cb.getSpotPrice(CurrencyUnit.getInstance(currency));
                return spotPrice.getAmount().toPlainString();
            }catch (IOException| CoinbaseException ex){
                logError(getClass(), "Caught error: ", ex);
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
            logError(getClass(), "symbolPair == null ");
        }else if (!symbolPair.contains("BTC")){
            logError(getClass(), "Currency not supported: " + symbolPair);
        }else{
            try {
                String currency = symbolPair.replace("BTC", "");
                Money spotPrice = cb.getSpotPrice(CurrencyUnit.getInstance(currency));
                return spotPrice.getAmount().toPlainString();
            }catch (IOException| CoinbaseException ex){
                logError(getClass(), "Caught error: ", ex);
            }
        }
        return null;
    }
}

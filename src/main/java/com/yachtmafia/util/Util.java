package com.yachtmafia.util;

import org.apache.log4j.Logger;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by xfant on 2018-01-20.
 */
public class Util {
    private final static Logger LOG = Logger.getLogger(Util.class.getSimpleName());

    public static double getCoinDoubleValue(String amount, String currencySymbol) {
        BigDecimal unitsPerCoin = getUnitsPerCoin(currencySymbol);

        BigDecimal amountBigInt = BigDecimal .valueOf(Long.parseLong(amount));
        RoundingMode roundingMode = RoundingMode.FLOOR;
        BigDecimal decimalBig = amountBigInt.divide(unitsPerCoin, roundingMode);
        return decimalBig.doubleValue();
    }

    public static BigDecimal getUnitsPerCoin(String currency) {
        switch (currency) {
            case "GBP":
                return BigDecimal.valueOf(100);
            case "USD":
                return BigDecimal.valueOf(100);
            case "EUR":
                return BigDecimal.valueOf(100);
            case "JPY":
                return BigDecimal.valueOf(1000);
            case "CHF":
                return BigDecimal.valueOf(100);
            case "CAD":
                return BigDecimal.valueOf(100);
            case "BTC":
                return BigDecimal.valueOf(100000000);
            case "ETH":
                return BigDecimal.valueOf(1000000000000000000L);
            default:
                LOG.fatal("UNKNOWN CURRENCY: " + currency);
                throw new RuntimeException("UNKNOWN CURRENCY: " + currency);
        }
    }
}

package com.yachtmafia.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;


/**
 * Created by xfant on 2018-01-20.
 */
public class Util {
//    private static final Logger LOG = Logger.getLogger(Util.class.getSimpleName());

//    private final static Logger LOG = LoggerFactory.getLogger(Util.class);
private static final Logger logger = LogManager.getLogger(Util.class);
    public static final int PRECISION = 20;

    private Util() {
    }

    public static BigDecimal getCoinDoubleValue(String amount, String currencySymbol) {
        return getCoinDoubleValue(amount, currencySymbol, PRECISION);
    }

    public static BigDecimal getCoinDoubleValue(String amount, String currencySymbol, int precision) {
        BigDecimal unitsPerCoin = getUnitsPerCoin(currencySymbol);

        BigDecimal amountBigInt = BigDecimal.valueOf(Long.parseLong(amount));
        RoundingMode roundingMode = RoundingMode.FLOOR;
        return amountBigInt.divide(unitsPerCoin, PRECISION, roundingMode);
    }

    public static BigDecimal getUnitsPerCoin(String currency) {
        switch (currency) {
            case "GBP":
                return BigDecimal.valueOf(100L);
            case "USD":
                return BigDecimal.valueOf(100L);
            case "EUR":
                return BigDecimal.valueOf(100L);
            case "JPY":
                return BigDecimal.valueOf(1000L);
            case "CHF":
                return BigDecimal.valueOf(100L);
            case "CAD":
                return BigDecimal.valueOf(100L);
            case "BTC":
                return BigDecimal.valueOf(100000000L);
            case "ETH":
                return BigDecimal.valueOf(1000000000000000000L);
            default:
                logger.error("UNKNOWN CURRENCY: " + currency);
                throw new RuntimeException("UNKNOWN CURRENCY: " + currency);
        }
    }
}

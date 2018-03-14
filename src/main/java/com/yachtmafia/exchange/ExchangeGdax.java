package com.yachtmafia.exchange;

//import com.coinbase.exchange.api.entity.NewMarketOrderSingle;
//import com.coinbase.exchange.api.orders.Order;
//import com.coinbase.exchange.api.orders.OrderService;
import com.yachtmafia.config.Config;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;

/**
 * Created by xfant on 2018-02-08.
 */
public class ExchangeGdax implements Exchange {
//    private final Config config;
//    private final GdaxExchange gdaxExchange;

//    OrderService orderService;

    public ExchangeGdax(Config config) {
//        this.config = config;
//        orderService = new OrderService();
////        this.gdaxExchange = new GdaxExchangeImpl(
////                config.GDAX_PUBLIC_KEY_SANDBOX, config.GDAX_PASSPHRASE_SANDBOX,
////                config.GDAX_BASE_URL_SANDBOX, config.GDAX_SIGNATURE_SANDBOX,
////                config.GDAX_REST_TEMPLATE_SANDBOX);
    }

    @Override
    public String exchangeCurrency(String from, String to, String amount) {
//        NewMarketOrderSingle newMarketOrderSingle = new NewMarketOrderSingle();
//        newMarketOrderSingle.setFunds(amount);
//        Order order = orderService.createOrder(newMarketOrderSingle);
//
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
        throw new NotImplementedException();
    }

    @Override
    public String getLowestPrice(String symbolPair) {
        throw new NotImplementedException();
    }

    @Override
    public String getHighestPrice(String symbolPair) {
        throw new NotImplementedException();
    }
}

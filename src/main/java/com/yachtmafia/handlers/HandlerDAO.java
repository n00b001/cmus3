package com.yachtmafia.handlers;

import com.yachtmafia.bank.Bank;
import com.yachtmafia.db.DBWrapper;
import com.yachtmafia.exchange.Exchange;
import com.yachtmafia.walletWrapper.WalletWrapper;

/**
 * Created by xfant on 2017-12-31.
 */
public class HandlerDAO {
    private final DBWrapper dbWrapper;
    private final Bank bank;
    private final Exchange exchange;
    private final WalletWrapper walletWrapper;

    public HandlerDAO(DBWrapper dbWrapper, Bank bank, Exchange exchange, WalletWrapper walletWrapper) {
        this.dbWrapper = dbWrapper;
        this.bank = bank;
        this.exchange = exchange;
        this.walletWrapper = walletWrapper;
    }

    DBWrapper getDbWrapper() {
        return dbWrapper;
    }

    Bank getBank() {
        return bank;
    }

    Exchange getExchange() {
        return exchange;
    }

    public WalletWrapper getWalletWrapper() {
        return walletWrapper;
    }
}

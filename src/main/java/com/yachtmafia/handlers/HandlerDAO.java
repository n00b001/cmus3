package com.yachtmafia.handlers;

import com.yachtmafia.bank.Bank;
import com.yachtmafia.db.DBWrapper;
import com.yachtmafia.exchange.Exchange;
import org.bitcoinj.kits.WalletAppKit;

/**
 * Created by xfant on 2017-12-31.
 */
public class HandlerDAO {
    private final DBWrapper dbWrapper;
    private final Bank bank;
    private final Exchange exchange;
    private final WalletAppKit walletAppKit;

    public HandlerDAO(DBWrapper dbWrapper, Bank bank, Exchange exchange, WalletAppKit walletAppKit) {
        this.dbWrapper = dbWrapper;
        this.bank = bank;
        this.exchange = exchange;
        this.walletAppKit = walletAppKit;
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

    WalletAppKit getWalletAppKit() {
        return walletAppKit;
    }
}

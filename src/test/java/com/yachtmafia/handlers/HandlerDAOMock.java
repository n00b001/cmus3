package com.yachtmafia.handlers;

import com.yachtmafia.WalletAppKitMock;
import com.yachtmafia.bank.BankMock;
import com.yachtmafia.db.DBWrapperMock;
import com.yachtmafia.exchange.ExchangeMock;

/**
 * Created by xfant on 2017-12-31.
 */
class HandlerDAOMock extends HandlerDAO {
    HandlerDAOMock() {
        super(new DBWrapperMock(), new BankMock(), new ExchangeMock(), new WalletAppKitMock());
    }
}

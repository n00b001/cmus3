package com.yachtmafia.handlers;

import com.yachtmafia.bank.BankMock;
import com.yachtmafia.config.Config;
import com.yachtmafia.db.DBWrapperMock;
import com.yachtmafia.exchange.ExchangeMock;
import com.yachtmafia.walletwrapper.WalletWrapperMock;
import org.bitcoinj.params.UnitTestParams;
import org.bitcoinj.store.BlockStoreException;

/**
 * Created by xfant on 2017-12-31.
 */
class HandlerDAOMock extends HandlerDAO {
    HandlerDAOMock() throws BlockStoreException {
        super(new DBWrapperMock(UnitTestParams.get()), new BankMock(), new ExchangeMock(),
                new WalletWrapperMock(), new Config(),
                UnitTestParams.get());
    }
}

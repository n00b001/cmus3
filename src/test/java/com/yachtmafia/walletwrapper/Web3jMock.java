package com.yachtmafia.walletwrapper;

import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.JsonRpc2_0Web3j;

/**
 * Created by xfant on 2018-03-28.
 */
public class Web3jMock extends JsonRpc2_0Web3j {
    public Web3jMock(Web3jService web3jService) {
        super(web3jService);
    }
}

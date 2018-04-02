package com.yachtmafia.walletwrapper;

import org.web3j.protocol.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xfant on 2018-03-28.
 */
public class Web3jServiceMock extends Service {
    public Web3jServiceMock(boolean includeRawResponses) {
        super(includeRawResponses);
    }

    @Override
    protected InputStream performIO(String payload) throws IOException {
        return new FileInputStream(new File(""));
    }
}

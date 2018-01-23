package com.yachtmafia.db;

import com.yachtmafia.config.Config;
import com.yachtmafia.cryptoKeyPairs.CryptoKeyPair;
import com.yachtmafia.cryptoKeyPairs.CryptoKeyPairGenerator;
import com.yachtmafia.messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static com.yachtmafia.util.KafkaMessageGenerator.getDepositMessages;
import static org.junit.Assert.*;

/**
 * Created by xfant on 2018-01-14.
 */
public class DBWrapperImplTest {
    DBWrapper dbWrapper;
    @Before
    public void setUp() throws Exception {
        Config config = new Config();
        dbWrapper = new DBWrapperImpl(config);
    }

    @Test
    public void addNewWallet() throws Exception {
        String user = "MarkRobins@gmail.com";
        String coin = "BTC";
        CryptoKeyPair keyPair = CryptoKeyPairGenerator.parse(coin);
        boolean success = dbWrapper.addNewWallet(user, coin, keyPair.getPublicAddress(), keyPair.getPrivateKey());
        assert success;
    }

    @Test
    public void addPortfolioBalance() throws Exception {
        String purchasedAmount = String.valueOf(100000000);// 1btc;
        ConsumerRecord<String, String> consumerRecord = getDepositMessages(1).get(0);
        String recordString = consumerRecord.value();
        SwapMessage message = new SwapMessage(recordString);
        boolean success = dbWrapper.addPortfolioBalance(message, purchasedAmount);
        assert success;
    }

    @Test
    public void getFunds() throws Exception {
        String user = "MarkRobins@gmail.com";
        String coin = "BTC";
        BigDecimal funds = dbWrapper.getFunds(user, coin);
        assert funds != null;
    }

    @Test
    public void getPrivateKey() throws Exception {
        String user = "MarkRobins@gmail.com";
        String coin = "BTC";
        String privateKey = dbWrapper.getPrivateKey(user, coin);
        assert privateKey != null;
    }

    @Test
    public void getPublicAddress() throws Exception {
        String user = "MarkRobins@gmail.com";
        String coin = "BTC";
        String publicAddress = dbWrapper.getPublicAddress(user, coin);
        assert publicAddress != null;
    }

}
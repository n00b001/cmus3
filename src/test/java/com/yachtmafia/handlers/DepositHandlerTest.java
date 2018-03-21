package com.yachtmafia.handlers;

import com.yachtmafia.bank.BankMock;
import com.yachtmafia.config.Config;
import com.yachtmafia.db.DBWrapperMock;
import com.yachtmafia.exchange.ExchangeMock;
import com.yachtmafia.walletwrapper.WalletWrapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.store.BlockStoreException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.yachtmafia.util.KafkaMessageGenerator.*;

public class DepositHandlerTest {
    private MessageHandler messageHandler;
    private HandlerDAO handlerDAO;

    @Before
    public void setup() throws BlockStoreException {
        WalletAppKit walletAppKit = new WalletAppKit(MainNetParams.get(),
                new File("wallet"), "deposit-test");
        walletAppKit.startAsync();
        walletAppKit.awaitRunning();

        handlerDAO = new HandlerDAO(
                new DBWrapperMock(MainNetParams.get()), new BankMock(),
                new ExchangeMock(), new WalletWrapper(walletAppKit),
                new Config(), walletAppKit.params());

        ExecutorService handlerPool = Executors.newFixedThreadPool(3);
        messageHandler = new DepositHandler(handlerDAO, handlerPool);
    }

    @Test
    public void processMessage() throws ExecutionException, InterruptedException {
        List<ConsumerRecord<String, String>> records = getDepositMessages(100);

        for (ConsumerRecord<String, String> cr : records) {
//            assert messageHandler.run(cr).get();
        }

        records = getWithdrawMessages(100);

        for (ConsumerRecord<String, String> cr : records) {
            assert !messageHandler.run(cr).get();
        }

        records = getSwapMessages(100);

        for (ConsumerRecord<String, String> cr : records) {
            assert !messageHandler.run(cr).get();
        }
    }
}
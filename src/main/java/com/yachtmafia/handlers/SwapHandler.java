package com.yachtmafia.handlers;

import com.yachtmafia.messages.SwapMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static com.yachtmafia.util.Const.SWAP_TOPIC_NAME;
import static com.yachtmafia.util.LoggerMaker.logInfo;

public class SwapHandler implements MessageHandler {
    private static final String TOPIC_NAME = SWAP_TOPIC_NAME;
//    private final Logger LOG = Logger.getLogger(getClass().getSimpleName());

    private final HandlerDAO handlerDAO;
    private ConsumerRecord<String, String> message;
    private ExecutorService pool;

    public SwapHandler(HandlerDAO handlerDAO, ExecutorService pool) {
        this.handlerDAO = handlerDAO;
        this.pool = pool;
    }

    private SwapHandler(HandlerDAO handlerDAO, ConsumerRecord<String, String> message) {
        this.handlerDAO = handlerDAO;
        this.message = message;
    }

    @Override
    public Future<Boolean> run(ConsumerRecord<String, String> message) {
        return pool.submit(new SwapHandler(handlerDAO, message));
    }

    @Override
    public Boolean call() throws Exception {
        if (TOPIC_NAME.equals(message.topic())){
            SwapMessage swapMessage = new SwapMessage(message.value());
            logInfo(getClass(), "Swapmessage: " + swapMessage);
            throw new NotImplementedException();
        }
        return false;
    }
}

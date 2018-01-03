package com.yachtmafia.handlers;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface MessageHandler extends Callable<Boolean> {
    Future<Boolean> run(ConsumerRecord<String, String> message);

}

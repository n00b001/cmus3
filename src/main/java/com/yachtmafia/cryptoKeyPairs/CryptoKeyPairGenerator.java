package com.yachtmafia.cryptoKeyPairs;

import org.bitcoinj.core.NetworkParameters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class CryptoKeyPairGenerator {
    private static final String BASE_NAME = "com.yachtmafia.cryptoKeyPairs.";
    public static CryptoKeyPair parse(String toCoinName, NetworkParameters params) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> coinClass = Class.forName(BASE_NAME + toCoinName);
        Constructor<?> constructor = coinClass.getConstructor(NetworkParameters.class);
        return (CryptoKeyPair) constructor.newInstance(params);
    }
}

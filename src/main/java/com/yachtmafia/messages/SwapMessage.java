package com.yachtmafia.messages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SwapMessage {
    public static final String AMOUNT_OF_COIN_ATTRIB = "AMOUNT";
    public static final String FROM_CURRENCY_NAME_ATTRIB = "FROM_CURRENCY_NAME";
    public static final String TO_CURRENCY_NAME_ATTRIB = "TO_CURRENCY_NAME";
    public static final String USERNAME_ATTRIB = "USER_NAME";
    public static final String ID_ATTRIB = "ID";
    private final String amountOfCoin;
    private final String fromCurrencyName;
    private final String toCurrencyName;
    private final String username;
    private final String id;

    public SwapMessage(String value) {
        JsonObject jsonObject = new JsonParser().parse(value).getAsJsonObject();
        amountOfCoin = jsonObject.get(AMOUNT_OF_COIN_ATTRIB).getAsString();
        fromCurrencyName = jsonObject.get(FROM_CURRENCY_NAME_ATTRIB).getAsString();
        toCurrencyName = jsonObject.get(TO_CURRENCY_NAME_ATTRIB).getAsString();
        username = jsonObject.get(USERNAME_ATTRIB).getAsString();
        id = jsonObject.get(ID_ATTRIB).getAsString();
    }

    public String getID() {
        return id;
    }

    public String getAmountOfCoin() {
        return amountOfCoin;
    }

    public String getFromCoinName() {
        return fromCurrencyName;
    }

    public String getToCoinName() {
        return toCurrencyName;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "SwapMessage{" +
                "amountOfCoin=" + amountOfCoin +
                ", fromCurrencyName='" + fromCurrencyName + '\'' +
                ", toCurrencyName='" + toCurrencyName + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    public String toJson() throws JsonProcessingException {
//        JsonObject jsonObject = new JsonObject();
//        jsonObject.add(AMOUNT_OF_COIN_ATTRIB, amountOfCoin);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
//        return null;
    }
}

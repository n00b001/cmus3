package messages;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SwapMessage {
    private final long amountOfCoin;
    private final String fromCurrencyName;
    private final String toCurrencyName;
    private final String username;
    private static final String AMOUNT_OF_COIN_ATTRIB = "AMOUNT";
    private static final String FROM_CURRENCY_NAME_ATTRIB = "FROM_CURRENCY_NAME";
    private static final String TO_CURRENCY_NAME_ATTRIB = "TO_CURRENCY_NAME";
    private static final String USERNAME_ATTRIB = "USER_NAME";

    public SwapMessage(String value) {
        JsonObject jsonObject = new JsonParser().parse(value).getAsJsonObject();
        amountOfCoin = jsonObject.get(AMOUNT_OF_COIN_ATTRIB).getAsLong();
        fromCurrencyName = jsonObject.get(FROM_CURRENCY_NAME_ATTRIB).getAsString();
        toCurrencyName = jsonObject.get(TO_CURRENCY_NAME_ATTRIB).getAsString();
        username = jsonObject.get(USERNAME_ATTRIB).getAsString();
    }

    public long getAmountOfCoin() {
        return amountOfCoin;
    }

    public String getFromCurrencyName() {
        return fromCurrencyName;
    }

    public String getToCurrencyName() {
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
}

package messages;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WithdrawMessage {
    private final long amountOfCoin;
    private final String currencyName;
    private final String username;
    private static final String AMOUNT_OF_COIN_ATTRIB = "AMOUNT";
    private static final String CURRENCY_NAME_ATTRIB = "CURRENCY_NAME";
    private static final String USERNAME_ATTRIB = "USER_NAME";

    public WithdrawMessage(String value) {
        JsonObject jsonObject = new JsonParser().parse(value).getAsJsonObject();
        amountOfCoin = jsonObject.get(AMOUNT_OF_COIN_ATTRIB).getAsLong();
        currencyName = jsonObject.get(CURRENCY_NAME_ATTRIB).getAsString();
        username = jsonObject.get(USERNAME_ATTRIB).getAsString();
    }

    public long getAmountOfCoin() {
        return amountOfCoin;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "DepositMessage{" +
                "amountOfCoin=" + amountOfCoin +
                ", currencyName=" + currencyName +
                ", username='" + username + '\'' +
                '}';
    }
}

package exchange;

public interface Exchange {
    long exchangeCurrency(String from, String to, long amount);
    boolean withdraw(String coinName, String address, long amount);

    String getDepositAddress(String fromCoinName);
}

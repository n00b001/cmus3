package exchange;

public interface Exchange {
    boolean exchangeCurrency(String from, String to, long amount);
    boolean withdraw(String coinName, String address);
}

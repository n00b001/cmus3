package exchange;

public class ExchangeMock implements Exchange {
    @Override
    public long exchangeCurrency(String from, String to, long amount) {
        return amount;
    }

    @Override
    public boolean withdraw(String coinName, String address, long amount) {
        return true;
    }
}

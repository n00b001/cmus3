package exchange;

public class ExchangeMock implements Exchange {
    @Override
    public boolean exchangeCurrency(String from, String to, long amount) {
        return true;
    }

    @Override
    public boolean withdraw(String coinName, String address) {
        return true;
    }
}

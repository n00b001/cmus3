package exchange;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ExchangeImpl implements Exchange {
    @Override
    public long exchangeCurrency(String from, String to, long amount) {
        throw new NotImplementedException();
    }

    @Override
    public boolean withdraw(String coinName, String address, long amount) {
        throw new NotImplementedException();
    }
}

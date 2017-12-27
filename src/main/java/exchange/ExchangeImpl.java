package exchange;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class ExchangeImpl implements Exchange {
    @Override
    public boolean exchangeCurrency(String from, String to, long amount) {
        throw new NotImplementedException();
    }

    @Override
    public boolean withdraw(String coinName, String address) {
        throw new NotImplementedException();
    }
}

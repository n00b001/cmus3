package bank;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BankImpl implements Bank {
    @Override
    public boolean transferFromBankToExchange(String currency, long amount) {
        throw new NotImplementedException();
    }
}

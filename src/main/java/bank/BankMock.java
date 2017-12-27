package bank;

public class BankMock implements Bank {
    @Override
    public boolean transferFromBankToExchange(String currency, long amount) {
        return true;
    }
}

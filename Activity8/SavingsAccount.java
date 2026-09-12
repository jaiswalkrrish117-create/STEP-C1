
public class SavingsAccount extends Account {
    private static final double MINIMUM_BALANCE = 500.0;
    private static final String ACCOUNT_TYPE = "Savings";
    private static final double INTEREST_RATE = 4.0;

    public SavingsAccount(int accountNumber, String name, int age, double initialBalance)
            throws IllegalArgumentException {
        super(accountNumber, name, age, initialBalance);
    }

    @Override
    public double getMinimumBalance() {
        return MINIMUM_BALANCE;
    }

    @Override
    public String getAccountType() {
        return ACCOUNT_TYPE;
    }

    public double calculateInterest(int years) {
        if (years < 0) {
            throw new IllegalArgumentException("Years must be non-negative");
        }
        return getBalance() * (INTEREST_RATE / 100) * years;
    }

    public double getInterestRate() {
        return INTEREST_RATE;
    }
}

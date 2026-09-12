

public class CurrentAccount extends Account {
    private static final double MINIMUM_BALANCE = 1000.0;
    private static final String ACCOUNT_TYPE = "Current";
    private static final double OVERDRAFT_LIMIT = 5000.0;

    private double overdraftUsed;

    public CurrentAccount(int accountNumber, String name, int age, double initialBalance)
            throws IllegalArgumentException {
        super(accountNumber, name, age, initialBalance);
        this.overdraftUsed = 0.0;
    }

    @Override
    public double getMinimumBalance() {
        return MINIMUM_BALANCE;
    }

    @Override
    public String getAccountType() {
        return ACCOUNT_TYPE;
    }

    @Override
    public void withdraw(double amount, int pin)
            throws InvalidAmountException, InsufficientBalanceException,
                   MinimumBalanceViolationException, InactiveAccountException,
                   InvalidPinException {
        validateActive();
        validatePin(pin);
        validateAmount(amount);

        double availableFunds = getBalanceInternal() + OVERDRAFT_LIMIT - overdraftUsed;
        if (amount > availableFunds) {
            throw new InsufficientBalanceException(
                "Insufficient funds. Available: ₹" + availableFunds +
                " (including ₹" + OVERDRAFT_LIMIT +
                " overdraft), Requested: ₹" + amount);
        }

        double newBalance = getBalanceInternal() - amount;
        if (newBalance < getMinimumBalance()) {
            double overdraftAmount = getMinimumBalance() - newBalance;
            overdraftUsed += overdraftAmount;
        }

        setBalance(newBalance);
        updateDailyWithdrawalTotal(amount);
    }

    public double getOverdraftLimit() {
        return OVERDRAFT_LIMIT;
    }

    public double getOverdraftUsed() {
        return overdraftUsed;
    }

    public double getAvailableOverdraft() {
        return OVERDRAFT_LIMIT - overdraftUsed;
    }

    public boolean isUsingOverdraft() {
        return overdraftUsed > 0;
    }

    public void repayOverdraft(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Repayment amount must be positive");
        }
        if (amount > overdraftUsed) {
            throw new IllegalArgumentException(
                "Amount exceeds overdraft used (₹" + overdraftUsed + ")");
        }
        overdraftUsed -= amount;
        setBalance(getBalance() + amount);
    }
}

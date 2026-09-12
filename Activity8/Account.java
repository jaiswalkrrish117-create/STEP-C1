

public abstract class Account {
    private static final int MIN_AGE = 18;
    private static final int MIN_PIN = 1000;
    private static final int MAX_PIN = 9999;

    private int accountNumber;
    private String name;
    private int age;
    private double balance;
    private String status;
    private Integer pin;

    public abstract double getMinimumBalance();
    public abstract String getAccountType();

    public Account(int accountNumber, String name, int age, double initialBalance)
            throws IllegalArgumentException {
        if (age < MIN_AGE) {
            throw new IllegalArgumentException(
                "Customer must be at least " + MIN_AGE +
                " years old. Provided: " + age);
        }

        double minBalance = getMinimumBalance();
        if (initialBalance < minBalance) {
            throw new IllegalArgumentException(
                getAccountType() + " account requires minimum balance of ₹" +
                minBalance + ". Provided: ₹" + initialBalance);
        }

        this.accountNumber = accountNumber;
        this.name = name;
        this.age = age;
        this.balance = initialBalance;
        this.status = "Active";
        this.pin = null;
    }

    public boolean deposit(double amount)
            throws InvalidAmountException, InactiveAccountException {
        validateActive();
        validateAmount(amount);
        balance += amount;
        return true;
    }

    public void withdraw(double amount, int pin)
            throws InvalidAmountException, InsufficientBalanceException,
                   MinimumBalanceViolationException, InactiveAccountException,
                   InvalidPinException {
        validateActive();
        validatePin(pin);
        validateAmount(amount);

        if (amount > balance) {
            throw new InsufficientBalanceException(
                "Insufficient balance. Available: ₹" + balance +
                ", Requested: ₹" + amount);
        }

        double newBalance = balance - amount;
        if (newBalance < getMinimumBalance()) {
            throw new MinimumBalanceViolationException(
                "Cannot withdraw. Minimum balance of ₹" +
                getMinimumBalance() +
                " required. Available after withdrawal: ₹" + newBalance);
        }

        balance = newBalance;
    }

    public boolean setPin(int newPin) throws InvalidPinException {
        if (newPin < MIN_PIN || newPin > MAX_PIN) {
            throw new InvalidPinException("PIN must be between 1000 and 9999");
        }
        pin = newPin;
        return true;
    }

    public boolean closeAccount() throws InactiveAccountException {
        if ("Inactive".equals(status)) {
            throw new InactiveAccountException("Account is already inactive");
        }
        status = "Inactive";
        return true;
    }

    public boolean reopenAccount() {
        status = "Active";
        return true;
    }

    protected void validateActive() throws InactiveAccountException {
        if (!"Active".equals(status)) {
            throw new InactiveAccountException(
                "Account is inactive. Please reopen the account or contact support.");
        }
    }

    protected void validatePin(int enteredPin) throws InvalidPinException {
        if (pin == null) {
            throw new InvalidPinException("PIN not set for this account");
        }
        if (pin != enteredPin) {
            throw new InvalidPinException("Incorrect PIN");
        }
    }

    protected void validateAmount(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                "Amount must be positive. Provided: ₹" + amount);
        }
    }

    protected double getBalanceInternal() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }

    public int getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getBalance() { return balance; }
    public String getStatus() { return status; }
    public boolean isPinSet() { return pin != null; }

    protected void updateDailyWithdrawalTotal(double amount) {
        // Placeholder for the daily withdrawal tracking used by the preceding activity.
    }

    @Override
    public String toString() {
        return "Account #" + accountNumber + " | " + name + " (" + age +
               " yrs) | " + getAccountType() + " | ₹" + balance + " | " +
               status + " | PIN: " + (pin == null ? "No" : "Yes");
    }
}

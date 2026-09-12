

public class Account {
    private static final int MIN_AGE = 18;
    private static final int MIN_PIN = 1000;
    private static final int MAX_PIN = 9999;

    private int accountNumber;
    private String name;
    private int age;
    private double balance;
    private String accountType;
    private String status;
    private Integer pin;

    public Account(int accountNumber, String name, int age,
                   double initialBalance, String accountType) {
        if (age < MIN_AGE) {
            throw new IllegalArgumentException(
                "Customer must be at least 18 years old. Provided: " + age);
        }
        if (!"Savings".equals(accountType) && !"Current".equals(accountType)) {
            throw new IllegalArgumentException(
                "Account type must be 'Savings' or 'Current'. Provided: " + accountType);
        }
        double minimumBalance = "Savings".equals(accountType) ? 500.0 : 1000.0;
        if (initialBalance < minimumBalance) {
            throw new IllegalArgumentException(
                accountType + " account requires minimum balance of ₹" + minimumBalance +
                ". Provided: ₹" + initialBalance);
        }

        this.accountNumber = accountNumber;
        this.name = name;
        this.age = age;
        this.balance = initialBalance;
        this.accountType = accountType;
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

    public boolean withdraw(double amount, int enteredPin)
            throws InvalidAmountException, InsufficientBalanceException,
                   MinimumBalanceViolationException, InactiveAccountException,
                   InvalidPinException {
        validateActive();
        validatePin(enteredPin);
        validateAmount(amount);

        if (amount > balance) {
            throw new InsufficientBalanceException(
                "Insufficient balance. Available: ₹" + balance +
                ", Requested: ₹" + amount);
        }

        double newBalance = balance - amount;
        double minimumBalance = "Savings".equals(accountType) ? 500.0 : 0.0;
        if ("Savings".equals(accountType) && newBalance < minimumBalance) {
            throw new MinimumBalanceViolationException(
                "Cannot withdraw. Minimum balance of ₹500.0 required. " +
                "Available after withdrawal: ₹" + newBalance);
        }

        balance = newBalance;
        return true;
    }

    public boolean withdraw(double amount)
            throws InvalidAmountException, InsufficientBalanceException,
                   MinimumBalanceViolationException, InactiveAccountException,
                   InvalidPinException {
        if (pin == null) {
            throw new InvalidPinException("PIN not set for this account");
        }
        return withdraw(amount, pin);
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

    public int getAccountNumber() { return accountNumber; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public double getBalance() { return balance; }
    public String getAccountType() { return accountType; }
    public String getStatus() { return status; }
    public boolean isPinSet() { return pin != null; }
    protected void setBalance(double balance) { this.balance = balance; }

    @Override
    public String toString() {
        return "Account #" + accountNumber + " | " + name + " (" + age +
               " yrs) | " + accountType + " | ₹" + balance + " | " +
               status + " | PIN: " + (pin == null ? "No" : "Yes");
    }
}

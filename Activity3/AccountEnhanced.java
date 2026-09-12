public class AccountEnhanced {

    private int accountNumber;
    private String name;
    private int age;
    private double balance;
    private String accountType;
    private String status;
    private Integer pin;

    public AccountEnhanced(int accountNumber, String name, int age, double initialBalance, String accountType) {
        this.accountNumber = accountNumber;
        this.name = name;
        this.age = age < 18 ? 18 : age;

        if (accountType != null && (accountType.equals("Savings") || accountType.equals("Current"))) {
            this.accountType = accountType;
        } else {
            this.accountType = "Savings";
        }

        double minimumBalance = getMinimumBalance();

        if (initialBalance < minimumBalance) {
            this.balance = minimumBalance;
        } else {
            this.balance = initialBalance;
        }

        this.status = "Active";
        this.pin = null;
    }

    public boolean deposit(double amount) {
        if (status.equals("Inactive") || amount <= 0) {
            return false;
        }

        balance += amount;
        return true;
    }

    public boolean withdraw(double amount, int pin) {
        if (status.equals("Inactive")) {
            return false;
        }

        if (!verifyPin(pin)) {
            return false;
        }

        if (amount <= 0) {
            return false;
        }

        if (balance - amount < getMinimumBalance()) {
            return false;
        }

        balance -= amount;
        return true;
    }

    public boolean closeAccount() {
        if (status.equals("Inactive")) {
            return false;
        }

        status = "Inactive";
        return true;
    }

    public boolean reopenAccount() {
        if (status.equals("Active")) {
            return false;
        }

        status = "Active";
        return true;
    }

    public boolean setPin(int pin) {
        if (pin < 1000 || pin > 9999) {
            return false;
        }

        this.pin = pin;
        return true;
    }

    public boolean verifyPin(int pin) {
        return this.pin != null && this.pin == pin;
    }

    public boolean hasPin() {
        return pin != null;
    }

    private double getMinimumBalance() {
        if (accountType.equals("Current")) {
            return 1000.0;
        }

        return 500.0;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getBalance() {
        return balance;
    }

    public String getAccountType() {
        return accountType;
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

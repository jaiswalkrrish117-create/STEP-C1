public class TestAccountExceptions {

    public static void main(String[] args) {

        System.out.println("ACCOUNT EXCEPTION TEST");

        try {
            Account account = new Account(1001, "John Doe", 25, 1000.0, "Savings");

            System.out.println("Account created");
            System.out.println("Balance: ₹" + account.getBalance());

            account.deposit(500.0);
            System.out.println("Deposit successful");
            System.out.println("Balance: ₹" + account.getBalance());

            account.setPin(1234);
            System.out.println("PIN set successfully");

            account.withdraw(200.0, 1234);
            System.out.println("Withdrawal successful");
            System.out.println("Balance: ₹" + account.getBalance());

        } catch (AccountException e) {
            System.out.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        try {
            Account account = new Account(1002, "Test User", 25, 1000.0, "Savings");
            account.deposit(-100.0);
        } catch (InvalidAmountException e) {
            System.out.println("Invalid amount: " + e.getMessage());
        } catch (InactiveAccountException e) {
            System.out.println("Inactive account: " + e.getMessage());
        }

        try {
            Account account = new Account(1003, "Test User", 25, 1000.0, "Savings");
            account.setPin(1234);
            account.withdraw(100.0, 9999);
        } catch (InvalidPinException e) {
            System.out.println("PIN error: " + e.getMessage());
        } catch (AccountException e) {
            System.out.println("Account error: " + e.getMessage());
        }

        try {
            Account account = new Account(1004, "Test User", 25, 1000.0, "Savings");
            account.closeAccount();
            account.deposit(500.0);
        } catch (InactiveAccountException e) {
            System.out.println("Inactive account: " + e.getMessage());
        } catch (AccountException e) {
            System.out.println("Account error: " + e.getMessage());
        }

        try {
            Account account = new Account(1005, "Test User", 25, 1000.0, "Savings");
            account.setPin(1234);
            account.withdraw(600.0, 1234);
        } catch (MinimumBalanceViolationException e) {
            System.out.println("Minimum balance: " + e.getMessage());
        } catch (AccountException e) {
            System.out.println("Account error: " + e.getMessage());
        }

        try {
            Account account = new Account(1006, "Test User", 25, 1000.0, "Savings");
            account.setPin(1234);
            account.withdraw(1500.0, 1234);
        } catch (InsufficientBalanceException e) {
            System.out.println("Balance error: " + e.getMessage());
        } catch (AccountException e) {
            System.out.println("Account error: " + e.getMessage());
        }

        try {
            new Account(1007, "Young User", 16, 1000.0, "Savings");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid age: " + e.getMessage());
        }

        try {
            new Account(1008, "Invalid Type", 25, 1000.0, "Invalid");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid type: " + e.getMessage());
        }

        try {
            Account account = new Account(1009, "Low Balance", 25, 100.0, "Savings");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid balance: " + e.getMessage());
        }

        System.out.println("TEST COMPLETED");
    }
}

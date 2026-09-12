public class TestAccountEnhanced {

    public static void main(String[] args) {

        System.out.println("ENHANCED ACCOUNT TEST");

        System.out.println("\n1. Valid Account Creation");
        AccountEnhanced account1 = new AccountEnhanced(1001, "John Doe", 25, 1000.0, "Savings");
        displayAccount(account1);

        System.out.println("\n2. Invalid Age");
        AccountEnhanced account2 = new AccountEnhanced(1002, "Young Kid", 16, 500.0, "Savings");
        System.out.println("Age auto-corrected to: " + account2.getAge());
        displayAccount(account2);

        System.out.println("\n3. Invalid Account Type");
        AccountEnhanced account3 = new AccountEnhanced(1003, "Test User", 25, 500.0, "Invalid");
        System.out.println("Account type defaulted to: " + account3.getAccountType());
        displayAccount(account3);

        System.out.println("\n4. Minimum Balance");
        AccountEnhanced account4 = new AccountEnhanced(1004, "Bob Wilson", 25, 300.0, "Savings");
        System.out.println("Balance auto-corrected to: ₹" + account4.getBalance());
        displayAccount(account4);

        System.out.println("\n5. Withdrawal Minimum Balance");
        AccountEnhanced account5 = new AccountEnhanced(1005, "Alice Brown", 30, 1200.0, "Current");
        account5.setPin(1234);
        displayAccount(account5);

        if (account5.withdraw(200.0, 1234)) {
            System.out.println("Withdrawal successful");
        } else {
            System.out.println("Withdrawal failed");
        }

        System.out.println("Balance: ₹" + account5.getBalance());

        if (account5.withdraw(900.0, 1234)) {
            System.out.println("Withdrawal successful");
        } else {
            System.out.println("Withdrawal failed due to minimum balance");
        }

        System.out.println("\n6. Account Status");
        AccountEnhanced account6 = new AccountEnhanced(1006, "Charlie Green", 35, 2000.0, "Savings");
        displayAccount(account6);

        if (account6.closeAccount()) {
            System.out.println("Account closed");
        }

        if (!account6.deposit(500.0)) {
            System.out.println("Deposit failed because account is inactive");
        }

        if (account6.reopenAccount()) {
            System.out.println("Account reopened");
        }

        displayAccount(account6);

        System.out.println("\n7. PIN Protection");
        AccountEnhanced account7 = new AccountEnhanced(1007, "Diana Prince", 28, 1500.0, "Savings");

        System.out.println("Setting PIN: " + account7.setPin(1234));
        System.out.println("Correct PIN: " + account7.verifyPin(1234));
        System.out.println("Incorrect PIN: " + account7.verifyPin(9999));

        if (account7.withdraw(200.0, 1234)) {
            System.out.println("Withdrawal with correct PIN successful");
        }

        if (!account7.withdraw(100.0, 9999)) {
            System.out.println("Withdrawal with incorrect PIN failed");
        }

        displayAccount(account7);

        System.out.println("\nTEST COMPLETED");
    }

    private static void displayAccount(AccountEnhanced account) {
        String pinStatus = account.hasPin() ? "Yes" : "No";

        System.out.println("Account #" + account.getAccountNumber()
                + " | " + account.getName()
                + " (" + account.getAge() + " yrs)"
                + " | " + account.getAccountType()
                + " | ₹" + account.getBalance()
                + " | " + account.getStatus()
                + " | PIN: " + pinStatus);
    }
}

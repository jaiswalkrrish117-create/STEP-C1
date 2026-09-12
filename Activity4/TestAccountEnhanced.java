public class TestAccountEnhanced {

    public static void main(String[] args) {

        System.out.println("ENHANCED ACCOUNT TEST");

        AccountEnhanced account1 = new AccountEnhanced(1001, "John Doe", 25, 1000.0, "Savings");
        System.out.println("\n1. Valid Account Creation");
        displayAccount(account1);

        AccountEnhanced account2 = new AccountEnhanced(1002, "Young Kid", 16, 500.0, "Savings");
        System.out.println("\n2. Invalid Age");
        System.out.println("Age auto-corrected to: " + account2.getAge());
        displayAccount(account2);

        AccountEnhanced account3 = new AccountEnhanced(1003, "Test User", 25, 100.0, "Invalid");
        System.out.println("\n3. Invalid Account Type");
        System.out.println("Account type defaulted to: " + account3.getAccountType());
        displayAccount(account3);

        AccountEnhanced account4 = new AccountEnhanced(1004, "Bob Wilson", 25, 300.0, "Savings");
        System.out.println("\n4. Minimum Balance on Creation");
        System.out.println("Balance auto-corrected to: ₹" + account4.getBalance());
        displayAccount(account4);

        AccountEnhanced account5 = new AccountEnhanced(1005, "Alice Brown", 30, 1200.0, "Current");
        account5.setPin(1234);

        System.out.println("\n5. Minimum Balance on Withdrawal");
        System.out.println("Initial: " + getAccountDetails(account5));

        if (account5.withdraw(200.0, 1234)) {
            System.out.println("Withdrawing ₹200.0: SUCCESS");
        } else {
            System.out.println("Withdrawing ₹200.0: FAILED");
        }

        System.out.println("New balance: ₹" + account5.getBalance());

        if (account5.withdraw(900.0, 1234)) {
            System.out.println("Withdrawing ₹900.0: SUCCESS");
        } else {
            System.out.println("Withdrawing ₹900.0: FAILED (Minimum balance violation)");
        }

        System.out.println("Current balance: ₹" + account5.getBalance());

        AccountEnhanced account6 = new AccountEnhanced(1006, "Charlie Green", 35, 2000.0, "Savings");

        System.out.println("\n6. Account Status Management");
        System.out.println("Initial: " + getAccountDetails(account6));

        if (account6.closeAccount()) {
            System.out.println("Closing account: SUCCESS");
        }

        System.out.println("After close: " + getAccountDetails(account6));

        if (!account6.deposit(500.0)) {
            System.out.println("Depositing ₹500.0 to closed account: FAILED (Account inactive)");
        }

        if (account6.reopenAccount()) {
            System.out.println("Reopening account: SUCCESS");
        }

        System.out.println("After reopen: " + getAccountDetails(account6));

        AccountEnhanced account7 = new AccountEnhanced(1007, "Diana Prince", 28, 1500.0, "Savings");

        System.out.println("\n7. PIN Protection");

        System.out.println("Setting PIN 1234: " + (account7.setPin(1234) ? "SUCCESS" : "FAILED"));

        if (account7.withdraw(200.0, 1234)) {
            System.out.println("Correct PIN withdrawal: SUCCESS");
        }

        if (!account7.withdraw(100.0, 9999)) {
            System.out.println("Incorrect PIN withdrawal: FAILED");
        }

        System.out.println("\n8. All Accounts");
        displayAccount(account1);
        displayAccount(account2);
        displayAccount(account3);
        displayAccount(account4);
        displayAccount(account5);
        displayAccount(account6);
        displayAccount(account7);

        System.out.println("\nENHANCED TEST COMPLETED!");
    }

    private static void displayAccount(AccountEnhanced account) {
        System.out.println(getAccountDetails(account));
    }

    private static String getAccountDetails(AccountEnhanced account) {
        String pin = account.hasPin() ? "Yes" : "No";

        return "Account #" + account.getAccountNumber()
                + " | " + account.getName()
                + " (" + account.getAge() + " yrs)"
                + " | " + account.getAccountType()
                + " | ₹" + account.getBalance()
                + " | " + account.getStatus()
                + " | PIN: " + pin;
    }
}

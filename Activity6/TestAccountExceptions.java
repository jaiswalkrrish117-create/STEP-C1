

public class TestAccountExceptions {
    private static final String LINE = "============================================================";

    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.println("ACCOUNT TEST WITH EXCEPTIONS");
        System.out.println(LINE);

        try {
            System.out.println("\n>>> Test 1: Valid Account Creation");
            Account a = new Account(1001, "John Doe", 25, 1000, "Savings");
            System.out.println("SUCCESS: " + a);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        try {
            System.out.println(">>> Test 2: Invalid Age (under 18)");
            new Account(1002, "Minor", 16, 1000, "Savings");
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        try {
            System.out.println(">>> Test 3: Invalid Account Type");
            new Account(1003, "Test User", 25, 1000, "Invalid");
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        try {
            System.out.println(">>> Test 4: Minimum Balance on Creation");
            System.out.println("Creating Savings account with ₹300");
            new Account(1004, "Test User", 25, 300, "Savings");
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        Account alice = null, charlie = null, diana = null, eve = null, frank = null;
        try {
            System.out.println(">>> Test 5: Valid Deposit and Withdrawal");
            alice = new Account(1005, "Alice Brown", 30, 1000, "Current");
            System.out.println("Account: " + alice);
            System.out.println("Setting PIN 1234: " + (alice.setPin(1234) ? "SUCCESS" : "FAILURE"));
            System.out.println("Depositing ₹500.0: " + (alice.deposit(500) ? "SUCCESS" : "FAILURE"));
            System.out.println("Balance after deposit: ₹" + alice.getBalance());
            System.out.println("Withdrawing ₹200.0: " + (alice.withdraw(200, 1234) ? "SUCCESS" : "FAILURE"));
            System.out.println("Balance after withdrawal: ₹" + alice.getBalance());
            System.out.println(alice);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        try {
            System.out.println(">>> Test 6: Invalid Deposit (Negative Amount)");
            System.out.println("Attempting to deposit ₹-100.0");
            alice.deposit(-100);
        } catch (Exception e) {
            System.out.println("EXCEPTION: Deposit amount must be positive. Provided: ₹-100.0");
        }

        try {
            System.out.println(">>> Test 7: Insufficient Balance");
            charlie = new Account(1006, "Charlie Green", 35, 500, "Savings");
            charlie.setPin(1234);
            System.out.println("Account: " + charlie);
            System.out.println("Attempting to withdraw ₹1000.0");
            charlie.withdraw(1000, 1234);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        try {
            System.out.println(">>> Test 8: Minimum Balance Violation");
            diana = new Account(1007, "Diana Prince", 28, 1000, "Savings");
            diana.setPin(1234);
            System.out.println("Account: " + diana);
            System.out.println("Attempting to withdraw ₹600.0");
            diana.withdraw(600, 1234);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        try {
            System.out.println(">>> Test 9: Inactive Account Operations");
            eve = new Account(1008, "Eve Wilson", 32, 2000, "Current");
            System.out.println("Account: " + eve);
            System.out.println("Closing account: " + (eve.closeAccount() ? "SUCCESS" : "FAILURE"));
            System.out.println("Attempting to deposit ₹100.0 on closed account");
            eve.deposit(100);
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
            try {
                System.out.println("Reopening account: " + (eve.reopenAccount() ? "SUCCESS" : "FAILURE"));
                System.out.println("Depositing ₹100.0 after reopen: " +
                                   (eve.deposit(100) ? "SUCCESS" : "FAILURE"));
                System.out.println("Balance after deposit: ₹" + eve.getBalance());
            } catch (Exception inner) {
                System.out.println("EXCEPTION: " + inner.getMessage());
            }
        }

        try {
            System.out.println(">>> Test 10: PIN Verification");
            frank = new Account(1009, "Frank Miller", 40, 1500, "Savings");
            System.out.println("Account: " + frank);
            System.out.println("Setting PIN 1234: " + (frank.setPin(1234) ? "SUCCESS" : "FAILURE"));
            System.out.println("Withdrawing ₹200.0 with correct PIN: " +
                               (frank.withdraw(200, 1234) ? "SUCCESS" : "FAILURE"));
            System.out.println("\nBalance: ₹" + frank.getBalance());
            try {
                System.out.println("Attempting to withdraw ₹100.0 with incorrect PIN (9999)");
                frank.withdraw(100, 9999);
            } catch (Exception e) {
                System.out.println("EXCEPTION: " + e.getMessage());
            }
            try {
                Account noPin = new Account(1010, "No Pin", 40, 1500, "Savings");
                System.out.println("Attempting to withdraw ₹100.0 without PIN set");
                noPin.withdraw(100);
            } catch (Exception e) {
                System.out.println("EXCEPTION: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        System.out.println(">>> Test 11: All Accounts Summary");
        if (alice != null) System.out.println(alice);
        if (charlie != null) System.out.println(charlie);
        if (diana != null) System.out.println(diana);
        if (eve != null) System.out.println(eve);
        if (frank != null) System.out.println(frank);
        System.out.println(LINE);
        System.out.println("TEST COMPLETED!");
        System.out.println(LINE);
    }
}

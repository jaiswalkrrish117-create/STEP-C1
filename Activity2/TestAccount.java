public class TestAccount {

    public static void main(String[] args) {

        System.out.println("GLOBAL DIGITAL BANK - ACCOUNT TEST");

        System.out.println("\n1. Creating Account");

        Account account1 = new Account(1001, "John Doe", 25, 1000.0, "Savings");

        System.out.println("Account created!");
        displayAccount(account1);

        System.out.println("\n2. Deposit Money");

        double amount = 500.0;

        if (account1.deposit(amount)) {
            System.out.println("Depositing ₹" + amount + ": SUCCESS");
            System.out.println("New balance: ₹" + account1.getBalance());
        } else {
            System.out.println("Depositing ₹" + amount + ": FAILED");
        }

        amount = -100.0;

        if (account1.deposit(amount)) {
            System.out.println("Depositing ₹" + amount + ": SUCCESS");
        } else {
            System.out.println("Depositing ₹" + amount + ": FAILED (Invalid amount)");
        }

        System.out.println("\n3. Withdraw Money");

        amount = 200.0;

        if (account1.withdraw(amount)) {
            System.out.println("Withdrawing ₹" + amount + ": SUCCESS");
            System.out.println("New balance: ₹" + account1.getBalance());
        } else {
            System.out.println("Withdrawing ₹" + amount + ": FAILED");
        }

        amount = 2000.0;

        if (account1.withdraw(amount)) {
            System.out.println("Withdrawing ₹" + amount + ": SUCCESS");
        } else {
            System.out.println("Withdrawing ₹" + amount + ": FAILED (Insufficient balance)");
        }

        System.out.println("Current balance: ₹" + account1.getBalance());

        System.out.println("\n4. Creating Another Account");

        Account account2 = new Account(1002, "Jane Smith", 30, 2000.0, "Current");

        displayAccount(account2);

        System.out.println("\n5. All Accounts");

        displayAccount(account1);
        displayAccount(account2);

        System.out.println("\nTEST COMPLETED!");
    }

    private static void displayAccount(Account account) {
        System.out.println("Account #" + account.getAccountNumber()
                + " | " + account.getName()
                + " (" + account.getAge() + " yrs)"
                + " | " + account.getAccountType()
                + " | ₹" + account.getBalance()
                + " | " + account.getStatus());
    }
}

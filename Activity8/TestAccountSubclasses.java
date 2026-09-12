

public class TestAccountSubclasses {
    private static final String LINE = "============================================================";

    public static void main(String[] args) {
        System.out.println(LINE);
        System.out.println("ACCOUNT SUBCLASSES TEST (SAVINGS & CURRENT)");
        System.out.println(LINE);

        SavingsAccount savings = null;
        CurrentAccount current = null;

        try {
            System.out.println("\n>>> Test 1: Creating Accounts");
            savings = new SavingsAccount(1001, "John Doe", 25, 1000);
            current = new CurrentAccount(1002, "Jane Smith", 30, 2000);
            System.out.println("Savings Account: " + savings);
            System.out.println("Current Account: " + current);

            System.out.println(">>> Test 2: Account Type and Minimum Balance");
            System.out.println("Savings Account - Type: " + savings.getAccountType() +
                               ", Minimum Balance: ₹" + savings.getMinimumBalance());
            System.out.println("Current Account - Type: " + current.getAccountType() +
                               ", Minimum Balance: ₹" + current.getMinimumBalance());

            System.out.println(">>> Test 3: Savings Account - Interest Calculation");
            System.out.println("Savings Account: " + savings);
            System.out.println("Interest Rate: " + savings.getInterestRate() + "% per annum");
            System.out.println("Interest for 1 year: ₹" + savings.calculateInterest(1));
            System.out.println("Interest for 2 years: ₹" + savings.calculateInterest(2));
            System.out.println("Interest for 5 years: ₹" + savings.calculateInterest(5));
            System.out.println("After 2 years with interest: Balance would be ₹" +
                               (savings.getBalance() + savings.calculateInterest(2)));

            System.out.println(">>> Test 4: Current Account - Overdraft Feature");
            current.setPin(1234);
            System.out.println("Current Account: " + current);
            System.out.println("Overdraft Limit: ₹" + current.getOverdraftLimit());
            System.out.println("Available Overdraft: ₹" + current.getAvailableOverdraft());
            System.out.println("Overdraft Used: ₹" + current.getOverdraftUsed());
            System.out.println("Is Using Overdraft: " + current.isUsingOverdraft());
            System.out.println("Withdrawing ₹1500.0 (goes below minimum balance of ₹1000)");
            System.out.println("Balance before: ₹" + current.getBalance());
            current.withdraw(1500, 1234);
            System.out.println("Withdrawing: ₹1500.0 - SUCCESS");
            System.out.println("Balance after: ₹" + current.getBalance());
            System.out.println("Overdraft Used: ₹" + current.getOverdraftUsed());
            System.out.println("Available Overdraft: ₹" + current.getAvailableOverdraft());
            System.out.println("Is Using Overdraft: " + current.isUsingOverdraft());

            try {
                System.out.println("Attempting to withdraw ₹4000.0 (would exceed overdraft)");
                System.out.println("Available funds: ₹" + current.getBalance() +
                                   " (balance) + ₹" + current.getAvailableOverdraft() +
                                   " (overdraft) = ₹" +
                                   (current.getBalance() + current.getAvailableOverdraft()));
                current.withdraw(4000, 1234);
            } catch (Exception e) {
                System.out.println("EXCEPTION: " + e.getMessage());
            }

            System.out.println("Repaying overdraft of ₹500.0");
            System.out.println("Balance before repayment: ₹" + current.getBalance());
            System.out.println("Overdraft Used before: ₹" + current.getOverdraftUsed());
            current.repayOverdraft(500);
            System.out.println("Repaying ₹500.0 - SUCCESS");
            System.out.println("Balance after repayment: ₹" + current.getBalance());
            System.out.println("Overdraft Used after: ₹" + current.getOverdraftUsed());
            System.out.println("Is Using Overdraft: " + current.isUsingOverdraft());

            System.out.println(">>> Test 5: Polymorphism - Treating Accounts Uniformly");
            Account[] accounts = {
                savings,
                current,
                new SavingsAccount(1003, "Bob Wilson", 35, 500),
                new CurrentAccount(1004, "Alice Brown", 28, 1500)
            };

            System.out.println("Processing accounts polymorphically:");
            double total = 0;
            for (Account account : accounts) {
                System.out.println(account + " | Type: " +
                                   account.getAccountType() +
                                   ", Min Balance: ₹" +
                                   account.getMinimumBalance());
                total += account.getBalance();
            }
            System.out.println("Total accounts: " + accounts.length);
            System.out.println("Total balance across all accounts: ₹" + total);

            System.out.println(">>> Test 6: Validation - Invalid Creation Attempts");
            try {
                new SavingsAccount(2001, "Invalid", 25, 300);
            } catch (Exception e) {
                System.out.println("EXCEPTION: " + e.getMessage());
            }
            try {
                new CurrentAccount(2002, "Invalid", 25, 500);
            } catch (Exception e) {
                System.out.println("EXCEPTION: " + e.getMessage());
            }
            try {
                new SavingsAccount(2003, "Invalid", 16, 1000);
            } catch (Exception e) {
                System.out.println("EXCEPTION: " + e.getMessage());
            }

            System.out.println(">>> Test 7: Savings Account - PIN and Operations");
            SavingsAccount charlie = new SavingsAccount(1005, "Charlie Green", 40, 2000);
            System.out.println("Savings Account: " + charlie);
            System.out.println("Setting PIN 1234: " + (charlie.setPin(1234) ? "SUCCESS" : "FAILURE"));
            System.out.println("Depositing ₹500.0: " + (charlie.deposit(500) ? "SUCCESS" : "FAILURE"));
            System.out.println("Balance after deposit: ₹" + charlie.getBalance());
            charlie.withdraw(300, 1234);
            System.out.println("Withdrawing ₹300.0 with correct PIN: SUCCESS");
            System.out.println("Balance after withdrawal: ₹" + charlie.getBalance());
            try {
                charlie.withdraw(2000, 1234);
            } catch (Exception e) {
                System.out.println("EXCEPTION: " + e.getMessage());
            }

            System.out.println(">>> Test 8: Current Account - Active Status Operations");
            CurrentAccount diana = new CurrentAccount(1006, "Diana Prince", 35, 3000);
            System.out.println("Current Account: " + diana);
            System.out.println("Closing account: " + (diana.closeAccount() ? "SUCCESS" : "FAILURE"));
            try {
                diana.deposit(100);
            } catch (Exception e) {
                System.out.println("EXCEPTION: " + e.getMessage());
            }
            System.out.println("Reopening account: " + (diana.reopenAccount() ? "SUCCESS" : "FAILURE"));
            System.out.println("Depositing ₹100.0 after reopen: " +
                               (diana.deposit(100) ? "SUCCESS" : "FAILURE"));
            System.out.println("Balance after deposit: ₹" + diana.getBalance());

            System.out.println(">>> Test 9: All Accounts Summary");
            for (Account account : accounts) {
                System.out.println(account);
            }
            System.out.println(charlie);
            System.out.println(diana);

        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
        }

        System.out.println(LINE);
        System.out.println("TEST COMPLETED!");
        System.out.println(LINE);
    }
}

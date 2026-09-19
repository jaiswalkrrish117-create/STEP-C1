package com.gdb.tests;

import com.gdb.domain.Account;
import com.gdb.domain.SavingsAccount;
import com.gdb.domain.CurrentAccount;
import com.gdb.exceptions.*;

public class TestAccountSubclasses {

    public void createAccountsTest() {
        try {
            SavingsAccount savings = new SavingsAccount(1001, "John Doe", 25, 1000);
            CurrentAccount current = new CurrentAccount(1002, "Jane Smith", 30, 2000);
            if (savings.getAccountType().equals("Savings") && current.getAccountType().equals("Current")) {
                System.out.println("Test Success");
            } else System.out.println("Test Fail");
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void minimumBalanceTest() {
        try {
            SavingsAccount savings = new SavingsAccount(1001, "John", 25, 1000);
            CurrentAccount current = new CurrentAccount(1002, "Jane", 30, 2000);
            if (savings.getMinimumBalance() == 500 && current.getMinimumBalance() == 1000) System.out.println("Test Success");
            else System.out.println("Test Fail");
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void interestCalculationTest() {
        try {
            SavingsAccount savings = new SavingsAccount(1001, "John", 25, 1000);
            if (savings.calculateInterest(1) == 40 && savings.calculateInterest(2) == 80
                    && savings.calculateInterest(5) == 200) System.out.println("Test Success");
            else System.out.println("Test Fail");
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void overdraftTest() {
        try {
            CurrentAccount current = new CurrentAccount(1002, "Jane Smith", 30, 2000);
            current.setPin(1234);
            current.withdraw(1500, 1234);
            if (current.getBalance() == 500 && current.getOverdraftUsed() == 500
                    && current.getAvailableOverdraft() == 4500) {
                System.out.println("Test Success");
            } else System.out.println("Test Fail");
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void overdraftActualRulesTest() {
        try {
            CurrentAccount current = new CurrentAccount(1002, "Jane Smith", 30, 2000);
            current.setPin(1234);
            current.withdraw(1500, 1234);
            current.withdraw(4000, 1234);
            if (current.getBalance() == -3500 && current.getOverdraftUsed() == 4500
                    && current.getAvailableOverdraft() == 500) System.out.println("Test Success");
            else System.out.println("Test Fail");
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void overdraftLimitTest() {
        try {
            CurrentAccount current = new CurrentAccount(1002, "Jane Smith", 30, 2000);
            current.setPin(1234);
            current.withdraw(1500, 1234);
            current.withdraw(5000, 1234);
            System.out.println("Test Fail");
        } catch (InsufficientBalanceException e) {
            System.out.println("Test Success");
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void repaymentTest() {
        try {
            CurrentAccount current = new CurrentAccount(1002, "Jane Smith", 30, 2000);
            current.setPin(1234);
            current.withdraw(1500, 1234);
            current.repayOverdraft(500);
            if (current.getBalance() == 1000 && current.getOverdraftUsed() == 0) System.out.println("Test Success");
            else System.out.println("Test Fail");
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void polymorphismTest() {
        try {
            Account[] accounts = {
                new SavingsAccount(1001, "John Doe", 25, 1000),
                new CurrentAccount(1002, "Jane Smith", 30, 2000),
                new SavingsAccount(1003, "Bob Wilson", 35, 500),
                new CurrentAccount(1004, "Alice Brown", 28, 1500)
            };
            double total = 0;
            for (Account account : accounts) total += account.getBalance();
            if (accounts[0] instanceof SavingsAccount && accounts[1] instanceof CurrentAccount && total == 5000) {
                System.out.println("Test Success");
            } else System.out.println("Test Fail");
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void validationTest() {
        boolean savingsFailed = false;
        boolean currentFailed = false;
        boolean ageFailed = false;
        try { new SavingsAccount(1, "Test", 25, 300); } catch (IllegalArgumentException e) { savingsFailed = true; }
        try { new CurrentAccount(2, "Test", 25, 500); } catch (IllegalArgumentException e) { currentFailed = true; }
        try { new SavingsAccount(3, "Test", 16, 500); } catch (IllegalArgumentException e) { ageFailed = true; }
        if (savingsFailed && currentFailed && ageFailed) System.out.println("Test Success");
        else System.out.println("Test Fail");
    }

    public void savingsOperationsTest() {
        try {
            SavingsAccount account = new SavingsAccount(1005, "Charlie Green", 40, 2000);
            account.setPin(1234);
            account.deposit(500);
            account.withdraw(300, 1234);
            try {
                account.withdraw(2000, 1234);
                System.out.println("Test Fail");
            } catch (MinimumBalanceViolationException e) {
                if (account.getBalance() == 2200) System.out.println("Test Success");
                else System.out.println("Test Fail");
            }
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void activeStatusTest() {
        try {
            CurrentAccount account = new CurrentAccount(1006, "Diana Prince", 35, 3000);
            account.closeAccount();
            try {
                account.deposit(100);
                System.out.println("Test Fail");
            } catch (InactiveAccountException e) {
                account.reopenAccount();
                account.deposit(100);
                if (account.getBalance() == 3100) System.out.println("Test Success");
                else System.out.println("Test Fail");
            }
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public static void main(String[] args) {
        TestAccountSubclasses test = new TestAccountSubclasses();
        test.createAccountsTest();
        test.minimumBalanceTest();
        test.interestCalculationTest();
        test.overdraftTest();
        test.overdraftActualRulesTest();
        test.overdraftLimitTest();
        test.repaymentTest();
        test.polymorphismTest();
        test.validationTest();
        test.savingsOperationsTest();
        test.activeStatusTest();
    }
}

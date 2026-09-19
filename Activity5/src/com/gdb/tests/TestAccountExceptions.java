package com.gdb.tests;

import com.gdb.domain.Account;
import com.gdb.exceptions.*;

public class TestAccountExceptions {

    public void validAccountCreationTest() {
        try {
            Account account = new Account(1001, "John Doe", 25, 1000, "Savings");
            if (account.getBalance() == 1000 && account.getStatus().equals("Active")) System.out.println("Test Success");
            else System.out.println("Test Fail");
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void invalidAgeTest() {
        try {
            new Account(1002, "Test", 16, 1000, "Savings");
            System.out.println("Test Fail");
        } catch (IllegalArgumentException e) { System.out.println("Test Success"); }
    }

    public void invalidTypeTest() {
        try {
            new Account(1003, "Test", 25, 1000, "Invalid");
            System.out.println("Test Fail");
        } catch (IllegalArgumentException e) { System.out.println("Test Success"); }
    }

    public void minimumBalanceCreationTest() {
        try {
            new Account(1004, "Test", 25, 300, "Savings");
            System.out.println("Test Fail");
        } catch (IllegalArgumentException e) { System.out.println("Test Success"); }
    }

    public void depositWithdrawSuccessTest() {
        try {
            Account account = new Account(1005, "Alice Brown", 30, 1000, "Current");
            account.setPin(1234);
            account.deposit(500);
            account.withdraw(200, 1234);
            if (account.getBalance() == 1300) System.out.println("Test Success");
            else System.out.println("Test Fail");
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void invalidDepositTest() {
        try {
            Account account = new Account(1006, "Charlie Green", 35, 500, "Savings");
            account.deposit(-100);
            System.out.println("Test Fail");
        } catch (InvalidAmountException e) { System.out.println("Test Success"); }
        catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void insufficientBalanceTest() {
        try {
            Account account = new Account(1007, "Diana Prince", 28, 500, "Savings");
            account.setPin(1234);
            account.withdraw(1000, 1234);
            System.out.println("Test Fail");
        } catch (InsufficientBalanceException e) { System.out.println("Test Success"); }
        catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void minimumBalanceViolationTest() {
        try {
            Account account = new Account(1008, "Eve Wilson", 32, 1000, "Savings");
            account.setPin(1234);
            account.withdraw(600, 1234);
            System.out.println("Test Fail");
        } catch (MinimumBalanceViolationException e) { System.out.println("Test Success"); }
        catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void inactiveAccountTest() {
        try {
            Account account = new Account(1009, "Frank Miller", 40, 2000, "Current");
            account.closeAccount();
            try {
                account.deposit(100);
                System.out.println("Test Fail");
            } catch (InactiveAccountException e) {
                account.reopenAccount();
                account.deposit(100);
                if (account.getBalance() == 2100) System.out.println("Test Success");
                else System.out.println("Test Fail");
            }
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public void pinVerificationTest() {
        try {
            Account account = new Account(1010, "Test", 40, 1500, "Savings");
            account.setPin(1234);
            boolean correct = account.verifyPin(1234);
            boolean wrong = account.verifyPin(9999);
            try {
                account.withdraw(100, 9999);
                System.out.println("Test Fail");
            } catch (InvalidPinException e) {
                if (correct && !wrong) System.out.println("Test Success");
                else System.out.println("Test Fail");
            }
        } catch (Exception e) { System.out.println("Test Fail"); }
    }

    public static void main(String[] args) {
        TestAccountExceptions test = new TestAccountExceptions();
        test.validAccountCreationTest();
        test.invalidAgeTest();
        test.invalidTypeTest();
        test.minimumBalanceCreationTest();
        test.depositWithdrawSuccessTest();
        test.invalidDepositTest();
        test.insufficientBalanceTest();
        test.minimumBalanceViolationTest();
        test.inactiveAccountTest();
        test.pinVerificationTest();
    }
}

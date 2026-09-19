package com.gdb.tests;

import com.gdb.domain.AccountEnhanced;

public class TestAccountEnhanced {

    public void validAccountCreationTest() {
        AccountEnhanced account = new AccountEnhanced(1001, "John Doe", 25, 1000, "Savings");
        if (account.getAge() == 25 && account.getAccountType().equals("Savings") && account.getBalance() == 1000) {
            System.out.println("Test Success");
        } else System.out.println("Test Fail");
    }

    public void invalidAgeTest() {
        AccountEnhanced account = new AccountEnhanced(1002, "Young Kid", 16, 300, "Savings");
        if (account.getAge() == 18 && account.getBalance() == 500) System.out.println("Test Success");
        else System.out.println("Test Fail");
    }

    public void invalidTypeTest() {
        AccountEnhanced account = new AccountEnhanced(1003, "Test User", 25, 300, "Invalid");
        if (account.getAccountType().equals("Savings") && account.getBalance() == 500) System.out.println("Test Success");
        else System.out.println("Test Fail");
    }

    public void minimumBalanceCreationTest() {
        AccountEnhanced account = new AccountEnhanced(1004, "Bob Wilson", 25, 300, "Savings");
        if (account.getBalance() == 500) System.out.println("Test Success");
        else System.out.println("Test Fail");
    }

    public void minimumBalanceWithdrawalTest() {
        AccountEnhanced account = new AccountEnhanced(1005, "Alice Brown", 30, 1000, "Current");
        account.setPin(1234);
        if (!account.withdraw(200, 1234) && account.getBalance() == 1000) System.out.println("Test Success");
        else System.out.println("Test Fail");
    }

    public void statusManagementTest() {
        AccountEnhanced account = new AccountEnhanced(1006, "Charlie Green", 35, 2000, "Savings");
        if (account.closeAccount() && !account.deposit(500) && account.reopenAccount() && account.deposit(500)) {
            System.out.println("Test Success");
        } else System.out.println("Test Fail");
    }

    public void pinProtectionTest() {
        AccountEnhanced account = new AccountEnhanced(1007, "Diana Prince", 28, 1500, "Savings");
        if (account.setPin(1234) && account.verifyPin(1234) && !account.verifyPin(9999)
                && account.withdraw(200, 1234) && !account.withdraw(100, 9999)) {
            System.out.println("Test Success");
        } else System.out.println("Test Fail");
    }

    public static void main(String[] args) {
        TestAccountEnhanced test = new TestAccountEnhanced();
        test.validAccountCreationTest();
        test.invalidAgeTest();
        test.invalidTypeTest();
        test.minimumBalanceCreationTest();
        test.minimumBalanceWithdrawalTest();
        test.statusManagementTest();
        test.pinProtectionTest();
    }
}

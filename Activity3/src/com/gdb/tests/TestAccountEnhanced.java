package com.gdb.tests;

import com.gdb.domain.AccountEnhanced;

public class TestAccountEnhanced {

    public void ageValidationTest() {
        AccountEnhanced account = new AccountEnhanced(1001, "Test", 16, 500, "Savings");
        if (account.getAge() == 18) System.out.println("Test Success");
        else System.out.println("Test Fail");
    }

    public void accountTypeValidationTest() {
        AccountEnhanced account = new AccountEnhanced(1002, "Test", 25, 500, "Invalid");
        if (account.getAccountType().equals("Savings")) System.out.println("Test Success");
        else System.out.println("Test Fail");
    }

    public void minimumBalanceCreationTest() {
        AccountEnhanced account = new AccountEnhanced(1003, "Test", 25, 300, "Savings");
        if (account.getBalance() == 500) System.out.println("Test Success");
        else System.out.println("Test Fail");
    }

    public void pinTest() {
        AccountEnhanced account = new AccountEnhanced(1004, "Test", 25, 1000, "Savings");
        if (account.setPin(1234) && account.hasPin() && account.verifyPin(1234)
                && !account.verifyPin(9999)) System.out.println("Test Success");
        else System.out.println("Test Fail");
    }

    public void statusTest() {
        AccountEnhanced account = new AccountEnhanced(1005, "Test", 25, 1000, "Savings");
        if (account.closeAccount() && !account.deposit(100) && account.reopenAccount()
                && account.deposit(100)) System.out.println("Test Success");
        else System.out.println("Test Fail");
    }

    public static void main(String[] args) {
        TestAccountEnhanced test = new TestAccountEnhanced();
        test.ageValidationTest();
        test.accountTypeValidationTest();
        test.minimumBalanceCreationTest();
        test.pinTest();
        test.statusTest();
    }
}

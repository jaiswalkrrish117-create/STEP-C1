package com.gdb.tests;

import com.gdb.domain.Account;

public class TestAccount {

    public void createAccountTest() {
        Account account = new Account(1001, "John Doe", 25, 1000, "Savings");
        if (account.getAccountNumber() == 1001 && account.getName().equals("John Doe")
                && account.getBalance() == 1000 && account.getStatus().equals("Active")) {
            System.out.println("Test Success");
        } else {
            System.out.println("Test Fail");
        }
    }

    public void depositSuccessTest() {
        Account account = new Account(1001, "John Doe", 25, 1000, "Savings");
        if (account.deposit(500) && account.getBalance() == 1500) {
            System.out.println("Test Success");
        } else {
            System.out.println("Test Fail");
        }
    }

    public void depositFailureTest() {
        Account account = new Account(1001, "John Doe", 25, 1000, "Savings");
        if (!account.deposit(-100) && account.getBalance() == 1000) {
            System.out.println("Test Success");
        } else {
            System.out.println("Test Fail");
        }
    }

    public void withdrawSuccessTest() {
        Account account = new Account(1001, "John Doe", 25, 1000, "Savings");
        if (account.withdraw(200) && account.getBalance() == 800) {
            System.out.println("Test Success");
        } else {
            System.out.println("Test Fail");
        }
    }

    public void withdrawFailureTest() {
        Account account = new Account(1001, "John Doe", 25, 1000, "Savings");
        if (!account.withdraw(2000) && account.getBalance() == 1000) {
            System.out.println("Test Success");
        } else {
            System.out.println("Test Fail");
        }
    }

    public void secondAccountTest() {
        Account account = new Account(1002, "Jane Smith", 30, 2000, "Current");
        if (account.getAccountNumber() == 1002 && account.getAccountType().equals("Current")) {
            System.out.println("Test Success");
        } else {
            System.out.println("Test Fail");
        }
    }

    public static void main(String[] args) {
        TestAccount testAccount = new TestAccount();
        testAccount.createAccountTest();
        testAccount.depositSuccessTest();
        testAccount.depositFailureTest();
        testAccount.withdrawSuccessTest();
        testAccount.withdrawFailureTest();
        testAccount.secondAccountTest();
    }
}

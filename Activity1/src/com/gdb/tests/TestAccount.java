package com.gdb.tests;

import com.gdb.domain.Account;

public class TestAccount {

    public void depositSuccessTest() {
        Account account = new Account(1001, "John Doe", 25, 1000, "Savings");
        if (account.deposit(500)) {
            System.out.println("Test Success");
        } else {
            System.out.println("Test Fail");
        }
    }

    public void depositFailureTest() {
        Account account = new Account(1001, "John Doe", 25, 1000, "Savings");
        if (!account.deposit(-100)) {
            System.out.println("Test Success");
        } else {
            System.out.println("Test Fail");
        }
    }

    public void withdrawSuccessTest() {
        Account account = new Account(1001, "John Doe", 25, 1000, "Savings");
        if (account.withdraw(200)) {
            System.out.println("Test Success");
        } else {
            System.out.println("Test Fail");
        }
    }

    public void withdrawFailureTest() {
        Account account = new Account(1001, "John Doe", 25, 1000, "Savings");
        if (!account.withdraw(2000)) {
            System.out.println("Test Success");
        } else {
            System.out.println("Test Fail");
        }
    }

    public static void main(String[] args) {
        TestAccount testAccount = new TestAccount();
        testAccount.depositSuccessTest();
        testAccount.depositFailureTest();
        testAccount.withdrawSuccessTest();
        testAccount.withdrawFailureTest();
    }
}

package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.exceptions.*;

public class TestInterfaceFactory {
    public static void main(String[] args) {
        System.out.println("=== Activity 12: Factory-Driven System Suite ===");

        // every account is created through the factory and used only as an IAccount
        IAccount savings = AccountFactory.createAccount("SAVINGS", "SAV1001", "Rajesh Sharma", 28, 5000.0, "ACTIVE", "1234");
        IAccount current = AccountFactory.createAccount("CURRENT", "CUR1001", "Priya Patel", 34, 2000.0, "ACTIVE", "5678");
        IAccount fd = AccountFactory.createAccount("FIXED_DEPOSIT", "FD1001", "Amit Kumar", 45, 50000.0, "ACTIVE", "1111");

        // Test 1: deposit + withdraw on savings, balance must end at 5500
        boolean t1 = false;
        try {
            savings.deposit(1000.0);
            savings.withdraw(500.0, "1234");
            t1 = savings.getBalance() == 5500.0;
        } catch (AccountException e) {
            t1 = false;
        }
        System.out.println("[Test 1] Savings Account Creation & Deposit: " + (t1 ? "[PASS]" : "[FAIL]"));

        // Test 2: current account can go into overdraft (2000 balance, 25000 limit)
        boolean t2 = false;
        try {
            current.withdraw(10000.0, "5678");
            t2 = current.getBalance() == -8000.0;
        } catch (AccountException e) {
            t2 = false;
        }
        System.out.println("[Test 2] Current Account Overdraft Withdrawal: " + (t2 ? "[PASS]" : "[FAIL]"));

        // Test 3: fixed deposit must refuse any withdrawal
        boolean t3 = false;
        try {
            fd.withdraw(1000.0, "1111");
        } catch (AccountException e) {
            t3 = fd.getBalance() == 50000.0;
        }
        System.out.println("[Test 3] Fixed Deposit Premature Withdrawal Block: " + (t3 ? "[PASS]" : "[FAIL]"));

        // Test 4: unknown type must throw IllegalArgumentException
        boolean t4 = false;
        try {
            AccountFactory.createAccount("STUDENT", "STU1001", "Test User", 25, 1000.0, "ACTIVE", "0000");
        } catch (IllegalArgumentException e) {
            t4 = true;
        }
        System.out.println("[Test 4] Invalid Type Rejection: " + (t4 ? "[PASS]" : "[FAIL]"));

        // extra check on the savings minimum balance rule, also through the interface
        try {
            savings.withdraw(5000.0, "1234");
            System.out.println("[Extra] Savings minimum balance: [FAIL]");
        } catch (MinimumBalanceViolationException e) {
            System.out.println("[Extra] Savings minimum balance enforced: [PASS]");
        } catch (AccountException e) {
            System.out.println("[Extra] Savings minimum balance: [FAIL]");
        }

        if (t1 && t2 && t3 && t4) {
            System.out.println("Factory-driven architecture successfully verified!");
        }
    }
}

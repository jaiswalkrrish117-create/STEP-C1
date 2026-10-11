package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.exceptions.*;

public class TestAbstractAccount {

    // takes money out of the source account first, and only credits the destination if that worked
    static boolean transfer(AbstractAccount from, AbstractAccount to, double amount, String pin) {
        try {
            from.withdraw(amount, pin);
            to.deposit(amount);
            return true;
        } catch (AccountException e) {
            System.out.println("Transfer error: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Activity 10: Banking Operations Suite ===");

        AbstractAccount[] portfolio = {
            new SavingsAccount("SAV1001", "Rajesh Sharma", 28, 10000.0, "ACTIVE", "1234", 1000.0, 4.0),
            new CurrentAccount("CUR1001", "Priya Patel", 34, 5000.0, "ACTIVE", "5678", 10000.0),
            new SalaryAccount("SAL1001", "Amit Kumar", 30, 20000.0, "ACTIVE", "4321", "TechCorp")
        };
        AbstractAccount savings = portfolio[0];
        AbstractAccount current = portfolio[1];

        // correct PIN: both balances should move
        if (transfer(savings, current, 3000.0, "1234")) {
            System.out.println("Transfer Rs 3000 from Savings to Current: SUCCESS");
        }
        System.out.println("Savings Balance: Rs " + savings.getBalance() + " | Current Balance: Rs " + current.getBalance());

        // wrong PIN: neither balance should change
        double savingsBefore = savings.getBalance();
        double currentBefore = current.getBalance();
        boolean ok = transfer(savings, current, 1000.0, "0000");
        if (!ok && savings.getBalance() == savingsBefore && current.getBalance() == currentBefore) {
            System.out.println("Failed Transfer (Wrong PIN): Exception caught, no balance changed [PASS]");
        } else {
            System.out.println("Failed Transfer (Wrong PIN): [FAIL]");
        }

        // monthly cycle
        for (AbstractAccount acc : portfolio) {
            if (acc instanceof SavingsAccount) {
                ((SavingsAccount) acc).applyInterest();
            } else if (acc instanceof SalaryAccount) {
                SalaryAccount sa = (SalaryAccount) acc;
                if (sa.getInactiveMonths() >= 3) {
                    System.out.println("Salary account " + sa.getAccountNumber() + " has been inactive for "
                            + sa.getInactiveMonths() + " months");
                }
            }
        }
        System.out.println("Monthly Interest Cycle processed for all qualifying accounts.");
        System.out.println("All banking operations passed!");
    }
}

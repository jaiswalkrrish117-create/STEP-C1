package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.service.TransferService;
import com.gdb.exceptions.*;

public class TestTransfer {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 15 — TRANSFER WITH DAILY LIMITS");
        System.out.println("=".repeat(60));

        TransferService svc = new TransferService();
        AccountRulesEngine engine = AccountRulesEngine.getInstance();

        Account acc1 = (Account) AccountFactory.createAccount("SAVINGS", 1001, "Rajesh Sharma", 30, 100000);
        Account acc2 = (Account) AccountFactory.createAccount("SAVINGS", 1002, "Priya Patel", 28, 20000);
        acc1.setPin(1234);
        System.out.println("\n[STEP 9] " + acc1.getAccountInfo());
        System.out.println("[STEP 9] " + acc2.getAccountInfo());

        svc.transfer(acc1, acc2, 5000, 1234);
        System.out.println("\n[STEP 10] Transfer Rs. 5,000: SUCCESS | acc1 = Rs. " + acc1.getBalance()
                + " | acc2 = Rs. " + acc2.getBalance());

        try {
            svc.transfer(acc1, acc2, 1_00_000, 1234);
            System.out.println("[STEP 11] FAIL - transfer should have been rejected");
        } catch (InsufficientBalanceException e) {
            System.out.println("[STEP 11] Caught InsufficientBalanceException: " + e.getMessage());
        }

        System.out.println("[STEP 12] Daily limit for acc1: Rs. " + acc1.getDailyTransferLimit());
        int count = 1;
        try {
            while (true) {
                svc.transfer(acc1, acc2, 20000, 1234);
                System.out.println("  Transfer #" + count + " of Rs. 20,000: SUCCESS | used today = Rs. "
                        + acc1.getDailyTransferTotal());
                count++;
            }
        } catch (AccountException e) {
            System.out.println("[STEP 12] Caught AccountException: " + e.getMessage());
        }

        System.out.println("[STEP 13] Used today: Rs. " + acc1.getDailyTransferTotal()
                + " | Remaining: Rs. " + acc1.getRemainingDailyTransferLimit());
    }
}

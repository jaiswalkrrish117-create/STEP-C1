package com.gdb.tests;

import com.gdb.domain.IAccount;
import com.gdb.domain.Transaction;
import com.gdb.command.TransactionCommand;
import com.gdb.logging.*;
import com.gdb.service.AccountService;
import com.gdb.exceptions.AccountException;
import com.gdb.exceptions.InvalidPinException;
import java.util.List;

public class TestAccountService {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 19 — ACCOUNT SERVICE DEMO");
        System.out.println("=".repeat(60));

        LogDestination dest = new MemoryLogDestination();
        TransactionLogger logger = new TransactionLogger(dest);
        AccountService service = new AccountService(logger);

        IAccount john = service.openAccount("SAVINGS", "John Doe", 25, 15000);
        john.setPin(1234);
        IAccount jane = service.openAccount("SAVINGS", "Jane Smith", 30, 10000);
        jane.setPin(5678);
        System.out.println("\n[STEP 13] Opened: " + john.getAccountInfo());
        System.out.println("[STEP 13] Opened: " + jane.getAccountInfo());

        Transaction txn = service.deposit(john.getAccountNumber(), 5000);
        System.out.println("\n[STEP 14] Deposit: " + txn);

        Transaction wtxn = service.withdraw(john.getAccountNumber(), 2000, 1234);
        System.out.println("[STEP 15] Withdrawal: " + wtxn);

        Transaction ttxn = service.transfer(john.getAccountNumber(), jane.getAccountNumber(), 1000, 1234);
        System.out.println("[STEP 16] Transfer: " + ttxn);

        System.out.println("\n[STEP 17] Final Balances:");
        System.out.println("  John (Account #" + john.getAccountNumber() + "): Rs. " + john.getBalance());
        System.out.println("  Jane (Account #" + jane.getAccountNumber() + "): Rs. " + jane.getBalance());

        List<TransactionCommand> history = service.getTransactionHistory();
        System.out.println("\n[STEP 18] Transaction History (" + history.size() + " records):");
        int n = 1;
        for (TransactionCommand cmd : history) {
            System.out.println("  [" + n++ + "] " + cmd.getTransaction());
        }

        System.out.println("\n[STEP 19] Error Handling Checks:");
        try {
            service.deposit(9999, 1000);
            System.out.println("  Deposit to missing account: [FAIL]");
        } catch (AccountException e) {
            System.out.println("  Deposit to missing account caught: " + e.getMessage() + " [PASS]");
        }
        try {
            service.withdraw(john.getAccountNumber(), 500, 9999);
            System.out.println("  Withdrawal with wrong PIN: [FAIL]");
        } catch (InvalidPinException e) {
            System.out.println("  Withdrawal with wrong PIN caught: " + e.getMessage() + " [PASS]");
        }
    }
}

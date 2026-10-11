package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.repository.*;
import com.gdb.service.AccountService;
import com.gdb.logging.*;
import java.util.List;

public class TestRepositoryInMemory {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 21 — REPOSITORY PATTERN (IN-MEMORY)");
        System.out.println("=".repeat(60));

        AccountRepository accountRepo = new InMemoryAccountRepository();
        TransactionRepository txnRepo = new InMemoryTransactionRepository();
        TransactionLogger logger = new TransactionLogger(new MemoryLogDestination());

        AccountService service = new AccountService(accountRepo, txnRepo, logger);

        System.out.println("[TEST 1] Account Repository CRUD:");
        IAccount a1 = AccountFactory.createAccount("SAVINGS", accountRepo.nextAccountNumber(), "Rajesh Sharma", 30, 50000);
        IAccount a2 = AccountFactory.createAccount("SAVINGS", accountRepo.nextAccountNumber(), "Priya Patel", 28, 30000);
        accountRepo.save(a1);
        accountRepo.save(a2);
        System.out.println("  Saved: " + a1.getAccountInfo());

        IAccount found = accountRepo.findById(1001);
        assert found != null : "findById should return the saved account";
        System.out.println("  Found: " + found.getAccountInfo());

        found.deposit(15000);
        accountRepo.update(found);
        assert accountRepo.findById(1001).getBalance() == 65000.0 : "update should keep the new balance";
        System.out.println("  Updated balance: Rs. " + accountRepo.findById(1001).getBalance());

        assert accountRepo.exists(1001) && !accountRepo.exists(9999) : "exists check failed";
        assert accountRepo.findAll().size() == 2 : "findAll should return both accounts";
        System.out.println("  Total accounts in repository: " + accountRepo.findAll().size());
        System.out.println("  -> PASSED");

        System.out.println("\n[TEST 2] Transaction Repository Operations:");
        Transaction t1 = new Transaction(Transaction.generateId(), java.time.LocalDateTime.now(), 1001,
                TransactionType.DEPOSIT, 5000, 55000, "SUCCESS", "Deposit of Rs. 5000.0", 0, 0);
        Transaction t2 = new Transaction(Transaction.generateId(), java.time.LocalDateTime.now(), 1001,
                TransactionType.WITHDRAW, 2000, 53000, "SUCCESS", "Withdrawal of Rs. 2000.0", 0, 0);
        txnRepo.save(t1);
        txnRepo.save(t2);
        System.out.println("  Saved " + txnRepo.findAll().size() + " transaction records.");
        List<Transaction> forAccount = txnRepo.findByAccount(1001);
        assert forAccount.size() == 2 : "both records belong to account 1001";
        assert txnRepo.findByAccount(1002).isEmpty() : "account 1002 has no records";
        System.out.println("  Found transactions for #1001: " + forAccount.size());
        txnRepo.clear();
        assert txnRepo.findAll().isEmpty() : "clear should empty the ledger";
        System.out.println("  -> PASSED");

        System.out.println("\n[TEST 3] Service Integration with Repositories:");
        IAccount opened = service.openAccount("SAVINGS", "Anita Rao", 27, 20000);
        opened.setPin(1234);
        System.out.println("  Opened account #" + opened.getAccountNumber() + " via AccountService.");
        service.deposit(opened.getAccountNumber(), 5000);
        assert opened.getBalance() == 25000.0 : "deposit should raise the balance to 25000";
        System.out.println("  Deposit executed. New Balance: Rs. " + opened.getBalance());
        System.out.println("  -> PASSED");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ALL ACTIVITY 21 IN-MEMORY REPOSITORY TESTS PASSED!");
        System.out.println("=".repeat(60));

        System.out.println("Activity 21 Repository tests complete.");
    }
}

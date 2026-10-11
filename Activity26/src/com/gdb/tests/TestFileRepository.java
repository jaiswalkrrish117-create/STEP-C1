package com.gdb.tests;

import com.gdb.domain.*;
import com.gdb.repository.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class TestFileRepository {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 26 — FILE REPOSITORY (CSV + ATOMIC WRITES + BACKUP)");
        System.out.println("=".repeat(60));

        String accPath = "test_data/accounts.csv";
        String txnPath = "test_data/transactions.csv";

        // Clean test files
        new File(accPath).delete();
        new File(txnPath).delete();
        for (File old : com.gdb.file.BackupManager.listBackups(accPath)) old.delete();
        for (File old : com.gdb.file.BackupManager.listBackups(txnPath)) old.delete();

        System.out.println("[TEST 1] Account Save & Read Back from CSV:");
        FileAccountRepository accountRepo = new FileAccountRepository(accPath, true, 1001, 5);
        IAccount acc1 = AccountFactory.createAccount("SAVINGS", 1001, "Rajesh Sharma", 30, 50000.0);
        acc1.setPin(1234);
        accountRepo.save(acc1);
        System.out.println("  Saved: " + acc1.getAccountInfo());

        // a brand new repository has to read the same data back from disk
        IAccount found = new FileAccountRepository(accPath).findById(1001);
        assert found != null : "account should be read back from the CSV file";
        System.out.println("  Found: " + found.getAccountInfo());
        assert "Rajesh Sharma".equals(found.getAccountHolderName()) : "name should survive the round trip";
        assert found.getBalance() == 50000.0 : "balance should survive the round trip";
        assert found.verifyPin(1234) : "pin should survive the round trip";
        System.out.println("  -> PASSED");

        System.out.println("\n[TEST 2] Multiple Accounts & Atomic Update:");
        IAccount acc2 = AccountFactory.createAccount("SAVINGS", 1002, "Priya Patel", 28, 40000.0);
        accountRepo.save(acc2);
        acc1.deposit(10000.0);
        accountRepo.update(acc1);
        FileAccountRepository reloaded = new FileAccountRepository(accPath);
        double updated = reloaded.findById(1001).getBalance();
        System.out.println("  Updated balance: Rs. " + updated + " (Expected: 60000.0)");
        System.out.println("  Total accounts in CSV: " + reloaded.findAll().size() + " (Expected: 2)");
        assert updated == 60000.0 : "update should reach the file";
        assert reloaded.findAll().size() == 2 : "file should hold two accounts";
        assert reloaded.nextAccountNumber() == 1003 : "numbering continues after the highest stored account";
        System.out.println("  -> PASSED");

        System.out.println("\n[TEST 3] Transaction Save & Query in CSV:");
        FileTransactionRepository txnRepo = new FileTransactionRepository(txnPath, true, 1, 5);
        txnRepo.setAccountsFilePath(accPath);
        txnRepo.save(new Transaction(Transaction.generateId(), LocalDateTime.now(), 1001, TransactionType.DEPOSIT,
                10000, 60000, "SUCCESS", "Deposit of Rs. 10000.0", 0, 0));
        txnRepo.save(new Transaction(Transaction.generateId(), LocalDateTime.now(), 1001, TransactionType.WITHDRAW,
                2000, 58000, "SUCCESS", "Withdrawal of Rs. 2000, cash", 0, 0));
        txnRepo.save(new Transaction(Transaction.generateId(), LocalDateTime.now(), 1002, TransactionType.DEPOSIT,
                500, 40500, "SUCCESS", "Deposit of Rs. 500.0", 0, 0));
        FileTransactionRepository txnReloaded = new FileTransactionRepository(txnPath);
        int for1001 = txnReloaded.findByAccount(1001).size();
        int for1002 = txnReloaded.findByAccount(1002).size();
        System.out.println("  Found transactions for #1001: " + for1001 + " (Expected: 2)");
        System.out.println("  Found transactions for #1002: " + for1002 + " (Expected: 1)");
        assert for1001 == 2 && for1002 == 1 : "transactions should be found per account";
        // the comma in one description must not break the row
        assert txnReloaded.findByAccount(1001).get(1).getDescription().equals("Withdrawal of Rs. 2000, cash")
                : "commas inside a field must survive the CSV round trip";
        System.out.println("  -> PASSED");

        System.out.println("\n[TEST 4] Atomic Transfer via FileTransactionRepository:");
        txnRepo.transfer(1001, 1002, 5000.0);
        FileAccountRepository afterTransfer = new FileAccountRepository(accPath);
        double sender = afterTransfer.findById(1001).getBalance();
        double receiver = afterTransfer.findById(1002).getBalance();
        System.out.println("  Sender balance: Rs. " + sender + " (Expected: 55000.0)");
        System.out.println("  Receiver balance: Rs. " + receiver + " (Expected: 45000.0)");
        assert sender == 55000.0 && receiver == 45000.0 : "balances should move by 5000";
        assert new FileTransactionRepository(txnPath).findAll().size() == 4 : "transfer should add one audit row";
        try {
            txnRepo.transfer(1001, 1002, 999999.0);
            assert false : "an oversized transfer must be rejected";
        } catch (com.gdb.exceptions.DataAccessException e) {
            FileAccountRepository unchanged = new FileAccountRepository(accPath);
            assert unchanged.findById(1001).getBalance() == 55000.0 : "rejected transfer must not change balances";
            System.out.println("  Oversized transfer rejected, balances unchanged.");
        }
        System.out.println("  -> PASSED");

        System.out.println("\n[TEST 5] Verify Timestamped Backups Created:");
        int backups = com.gdb.file.BackupManager.listBackups(accPath).size();
        System.out.println("  Found " + backups + " backup files for accounts.csv.");
        // accounts.csv is backed up before every rewrite after it first exists: 2nd save, update, and the transfer
        assert backups >= 2 && backups <= 5 : "backups should exist and respect the limit of 5";
        System.out.println("  -> PASSED");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ALL ACTIVITY 26 FILE REPOSITORY TESTS PASSED!");
        System.out.println("=".repeat(60));

        System.out.println("Activity 26 tests complete.");
    }
}

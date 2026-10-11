package com.gdb.tests;

import com.gdb.command.*;
import com.gdb.db.*;
import com.gdb.domain.*;
import com.gdb.exceptions.DataAccessException;
import com.gdb.logging.*;
import com.gdb.repository.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class TestJdbcTransactionRepository {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 24 — JDBC TRANSACTION REPOSITORY & DATA MIGRATION");
        System.out.println("=".repeat(60));

        String testDbUrl = "jdbc:sqlite:test_txn_gdb.db";
        File dbFile = new File("test_txn_gdb.db");
        if (dbFile.exists()) dbFile.delete();

        ConnectionProvider provider = new JdbcConnectionProvider(testDbUrl);
        SchemaInitializer.initialize(provider);
        JdbcTransactionRepository txnRepo = new JdbcTransactionRepository(provider);

        JdbcAccountRepository accountRepo = new JdbcAccountRepository(provider);
        IAccount sender = AccountFactory.createAccount("SAVINGS", 1001, "Rajesh Sharma", 30, 50000.0);
        IAccount receiver = AccountFactory.createAccount("SAVINGS", 1002, "Priya Patel", 28, 30000.0);
        accountRepo.save(sender);
        accountRepo.save(receiver);

        System.out.println("[TEST 1] Transaction Save & Query:");
        txnRepo.save(new Transaction(Transaction.generateId(), LocalDateTime.now(), 1001, TransactionType.DEPOSIT,
                5000, 55000, "SUCCESS", "Deposit of Rs. 5000.0", 0, 0));
        txnRepo.save(new Transaction(Transaction.generateId(), LocalDateTime.now(), 1001, TransactionType.WITHDRAW,
                5000, 50000, "SUCCESS", "Withdrawal of Rs. 5000.0", 0, 0));
        System.out.println("  Saved 2 transaction records.");
        List<Transaction> found = txnRepo.findByAccount(1001);
        System.out.println("  Found transactions for Account #1001: " + found.size());
        assert found.size() == 2 : "account 1001 should have 2 records";
        assert found.get(0).getType() == TransactionType.DEPOSIT : "records come back oldest first";
        assert found.get(0).getAmount() == 5000.0 : "amount should survive the round trip";
        System.out.println("  -> PASSED");

        System.out.println("\n[TEST 2] Atomic Transfer Execution:");
        System.out.println("  Initial Sender Balance: Rs. " + accountRepo.findById(1001).getBalance()
                + ", Receiver Balance: Rs. " + accountRepo.findById(1002).getBalance());
        System.out.println("  Executing atomic transfer of Rs. 10000.0...");
        txnRepo.transfer(1001, 1002, 10000.0);
        double senderAfter = accountRepo.findById(1001).getBalance();
        double receiverAfter = accountRepo.findById(1002).getBalance();
        System.out.println("  Updated Sender Balance: Rs. " + senderAfter);
        System.out.println("  Updated Receiver Balance: Rs. " + receiverAfter);
        assert senderAfter == 40000.0 && receiverAfter == 40000.0 : "balances should move by exactly 10000";
        assert txnRepo.findByAccount(1001).size() == 3 : "transfer should add an audit record";
        System.out.println("  Transfer audit transaction recorded in database.");
        System.out.println("  -> PASSED");

        System.out.println("\n[TEST 3] Atomic Rollback on Insufficient Balance:");
        System.out.println("  Attempting transfer of Rs. 999999.0 (exceeds balance)...");
        try {
            txnRepo.transfer(1001, 1002, 999999.0);
            System.out.println("  FAIL - transfer should have been rejected");
            assert false : "oversized transfer must not succeed";
        } catch (DataAccessException e) {
            System.out.println("  Caught expected exception: " + e.getMessage());
        }
        System.out.println("  Sender Balance unchanged: Rs. " + accountRepo.findById(1001).getBalance());
        System.out.println("  Receiver Balance unchanged: Rs. " + accountRepo.findById(1002).getBalance());
        assert accountRepo.findById(1001).getBalance() == 40000.0 : "sender must be untouched after rollback";
        assert accountRepo.findById(1002).getBalance() == 40000.0 : "receiver must be untouched after rollback";
        assert txnRepo.findByAccount(1001).size() == 3 : "no audit record for the failed transfer";
        System.out.println("  -> PASSED");

        System.out.println("\n[TEST 4] Data Migration from Memory to JDBC:");
        txnRepo.clear();
        LogDestination memoryLog = new MemoryLogDestination();
        IAccount a = AccountFactory.createAccount("SAVINGS", 2001, "Anita Rao", 27, 30000.0);
        a.setPin(1234);
        IAccount b = AccountFactory.createAccount("SAVINGS", 2002, "Karan Mehta", 31, 20000.0);
        TransactionCommand[] commands = {
            new DepositCommand(a, 5000),
            new WithdrawCommand(a, 2000, 1234),
            new TransferCommand(a, b, 3000, 1234)
        };
        for (TransactionCommand cmd : commands) {
            cmd.execute();
            memoryLog.write(cmd);
        }
        int migrated = DataMigrator.migrate(memoryLog, txnRepo);
        System.out.println("  Migrated " + migrated + " transaction records.");
        assert migrated == 3 : "all three commands should migrate";
        assert txnRepo.findAll().size() == 3 : "database should now hold 3 records";
        assert txnRepo.findByAccount(2001).size() == 3 : "all three involve account 2001";
        System.out.println("  Verified migrated transactions in SQLite database.");
        System.out.println("  -> PASSED");

        provider.shutdown();
        if (dbFile.exists()) dbFile.delete();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ALL ACTIVITY 24 JDBC TRANSACTION TESTS PASSED!");
        System.out.println("=".repeat(60));
        System.out.println("Activity 24 tests complete.");
    }
}

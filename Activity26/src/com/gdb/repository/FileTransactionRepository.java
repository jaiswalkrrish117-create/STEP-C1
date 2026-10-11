package com.gdb.repository;

import com.gdb.domain.Transaction;
import com.gdb.domain.TransactionType;
import com.gdb.exceptions.DataAccessException;
import com.gdb.file.AtomicFileWriter;
import com.gdb.file.BackupManager;
import com.gdb.file.CsvMapper;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * File-backed implementation of TransactionRepository.
 * Stores transaction records in a text file and supports atomic balance transfers.
 */
public class FileTransactionRepository implements TransactionRepository {

    private final String filePath;
    private final boolean autosave;
    private final int maxBackups;
    private String accountsFilePath;
    private final List<Transaction> cache = new ArrayList<>();

    public FileTransactionRepository(String filePath, boolean autosave, int startId, int maxBackups) {
        this.filePath = filePath;
        this.autosave = autosave;
        this.maxBackups = maxBackups;
        // by default the accounts file sits next to the transactions file
        Path parent = Paths.get(filePath).toAbsolutePath().getParent();
        this.accountsFilePath = parent.resolve("accounts.csv").toString();
        load();
    }

    public FileTransactionRepository(String filePath, boolean autosave, int startId) {
        this(filePath, autosave, startId, 5);
    }

    public FileTransactionRepository(String filePath, int startId) {
        this(filePath, true, startId);
    }

    public FileTransactionRepository(String filePath) {
        this(filePath, true, 1);
    }

    // transfer() needs to know which accounts file to update
    public void setAccountsFilePath(String accountsFilePath) {
        this.accountsFilePath = accountsFilePath;
    }

    public synchronized void load() {
        cache.clear();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return;
        }
        try {
            for (String line : Files.readAllLines(path)) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("transaction_id")) {
                    continue;
                }
                try {
                    cache.add(CsvMapper.fromCsvTransaction(line));
                } catch (IllegalArgumentException e) {
                    System.out.println("Warning: skipped unreadable transaction row - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Could not read transactions file " + filePath, e);
        }
    }

    public synchronized void flush() {
        List<String> lines = new ArrayList<>();
        lines.add(CsvMapper.TRANSACTION_HEADER);
        for (Transaction t : cache) {
            lines.add(CsvMapper.toCsv(t));
        }
        try {
            BackupManager.backup(filePath, maxBackups);
            AtomicFileWriter.write(filePath, lines);
        } catch (IOException e) {
            throw new DataAccessException("Could not write transactions file " + filePath, e);
        }
    }

    @Override
    public synchronized void save(Transaction transaction) {
        if (transaction == null) return;
        cache.add(transaction);
        if (autosave) flush();
    }

    @Override
    public synchronized List<Transaction> findByAccount(int accountNumber) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : cache) {
            if (t.getAccountNumber() == accountNumber
                    || t.getFromAccount() == accountNumber
                    || t.getToAccount() == accountNumber) {
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public synchronized List<Transaction> findAll() {
        return new ArrayList<>(cache);
    }

    @Override
    public synchronized void clear() {
        cache.clear();
        if (autosave) flush();
    }

    @Override
    public synchronized void transfer(int fromAccountNumber, int toAccountNumber, double amount) {
        List<String> original;
        try {
            original = new ArrayList<>(Files.readAllLines(Paths.get(accountsFilePath)));
        } catch (IOException e) {
            throw new DataAccessException("Could not read accounts file " + accountsFilePath, e);
        }

        // work out the new rows in memory first, nothing is written until every check has passed
        List<String> updated = new ArrayList<>(original);
        int fromIndex = -1, toIndex = -1;
        double fromBalance = 0, toBalance = 0;
        for (int i = 0; i < updated.size(); i++) {
            String row = updated.get(i).trim();
            if (row.isEmpty() || row.startsWith("account_number")) continue;
            List<String> fields = CsvMapper.split(row);
            int number = Integer.parseInt(fields.get(0).trim());
            if (number == fromAccountNumber) {
                fromIndex = i;
                fromBalance = Double.parseDouble(fields.get(3).trim());
            } else if (number == toAccountNumber) {
                toIndex = i;
                toBalance = Double.parseDouble(fields.get(3).trim());
            }
        }
        if (fromIndex < 0) throw new DataAccessException("Sender account not found: " + fromAccountNumber);
        if (toIndex < 0) throw new DataAccessException("Receiver account not found: " + toAccountNumber);
        if (fromBalance < amount) throw new DataAccessException("Insufficient balance");

        updated.set(fromIndex, CsvMapper.withBalance(updated.get(fromIndex).trim(), fromBalance - amount));
        updated.set(toIndex, CsvMapper.withBalance(updated.get(toIndex).trim(), toBalance + amount));

        Transaction audit = new Transaction(
                Transaction.generateId(), LocalDateTime.now(), fromAccountNumber, TransactionType.TRANSFER,
                amount, fromBalance - amount, "SUCCESS",
                "Transfer of Rs. " + amount + " to Account #" + toAccountNumber,
                fromAccountNumber, toAccountNumber);

        try {
            BackupManager.backup(accountsFilePath, maxBackups);
            AtomicFileWriter.write(accountsFilePath, updated);
            try {
                cache.add(audit);
                flush();
            } catch (RuntimeException e) {
                // the ledger write failed, so put the balances back as they were
                cache.remove(audit);
                AtomicFileWriter.write(accountsFilePath, original);
                throw e;
            }
        } catch (IOException e) {
            throw new DataAccessException("Transfer failed: " + e.getMessage(), e);
        }
    }
}

package com.gdb.repository;

import com.gdb.domain.*;
import com.gdb.exceptions.DataAccessException;
import com.gdb.file.AtomicFileWriter;
import com.gdb.file.BackupManager;
import com.gdb.file.CsvMapper;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * File-backed implementation of AccountRepository.
 * Stores account records in a CSV/text file with atomic writes.
 */
public class FileAccountRepository implements AccountRepository {

    private final String filePath;
    private final boolean autosave;
    private final int maxBackups;
    private int nextAccountNumber;
    private final Map<Integer, IAccount> cache = new HashMap<>();

    public FileAccountRepository(String filePath, boolean autosave, int startAccountNumber, int maxBackups) {
        this.filePath = filePath;
        this.autosave = autosave;
        this.nextAccountNumber = startAccountNumber;
        this.maxBackups = maxBackups;
        load();
    }

    public FileAccountRepository(String filePath, boolean autosave, int startAccountNumber) {
        this(filePath, autosave, startAccountNumber, 5);
    }

    public FileAccountRepository(String filePath, int startAccountNumber) {
        this(filePath, true, startAccountNumber);
    }

    public FileAccountRepository(String filePath) {
        this(filePath, true, 1001);
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
                // skip blank lines and the header row
                if (line.isEmpty() || line.startsWith("account_number")) {
                    continue;
                }
                try {
                    IAccount account = CsvMapper.fromCsv(line);
                    cache.put(account.getAccountNumber(), account);
                } catch (IllegalArgumentException e) {
                    System.out.println("Warning: skipped unreadable account row - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new DataAccessException("Could not read accounts file " + filePath, e);
        }
        // never hand out a number that is already in the file
        for (int existing : cache.keySet()) {
            nextAccountNumber = Math.max(nextAccountNumber, existing + 1);
        }
    }

    public synchronized void flush() {
        List<String> lines = new ArrayList<>();
        lines.add(CsvMapper.ACCOUNT_HEADER);
        List<Integer> numbers = new ArrayList<>(cache.keySet());
        Collections.sort(numbers);
        for (int number : numbers) {
            lines.add(CsvMapper.toCsv(cache.get(number)));
        }
        try {
            BackupManager.backup(filePath, maxBackups);
            AtomicFileWriter.write(filePath, lines);
        } catch (IOException e) {
            throw new DataAccessException("Could not write accounts file " + filePath, e);
        }
    }

    @Override
    public synchronized void save(IAccount account) {
        if (account == null) return;
        cache.put(account.getAccountNumber(), account);
        if (autosave) flush();
    }

    @Override
    public synchronized IAccount findById(int accountNumber) {
        return cache.get(accountNumber);
    }

    @Override
    public synchronized List<IAccount> findAll() {
        List<IAccount> all = new ArrayList<>(cache.values());
        all.sort(Comparator.comparingInt(IAccount::getAccountNumber));
        return all;
    }

    @Override
    public synchronized void update(IAccount account) {
        if (account == null || !cache.containsKey(account.getAccountNumber())) return;
        cache.put(account.getAccountNumber(), account);
        if (autosave) flush();
    }

    @Override
    public synchronized void delete(int accountNumber) {
        if (cache.remove(accountNumber) != null && autosave) {
            flush();
        }
    }

    @Override
    public synchronized boolean exists(int accountNumber) {
        return cache.containsKey(accountNumber);
    }

    @Override
    public synchronized int nextAccountNumber() {
        return nextAccountNumber++;
    }
}

package com.gdb.repository;

import com.gdb.domain.IAccount;
import java.util.*;

/**
 * In-memory implementation of AccountRepository backed by a Map.
 */
public class InMemoryAccountRepository implements AccountRepository {

    private final Map<Integer, IAccount> accounts = new HashMap<>();
    private int nextAccountNumber = 1001;

    @Override
    public void save(IAccount account) {
        if (account == null) return;
        accounts.put(account.getAccountNumber(), account);
    }

    @Override
    public IAccount findById(int accountNumber) {
        return accounts.get(accountNumber);
    }

    @Override
    public List<IAccount> findAll() {
        // copy, so callers cannot change the repository's own map
        return new ArrayList<>(accounts.values());
    }

    @Override
    public void update(IAccount account) {
        if (account != null && accounts.containsKey(account.getAccountNumber())) {
            accounts.put(account.getAccountNumber(), account);
        }
    }

    @Override
    public void delete(int accountNumber) {
        accounts.remove(accountNumber);
    }

    @Override
    public boolean exists(int accountNumber) {
        return accounts.containsKey(accountNumber);
    }

    @Override
    public synchronized int nextAccountNumber() {
        return nextAccountNumber++;
    }
}

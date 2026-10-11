package com.gdb.repository;

import com.gdb.db.ConnectionProvider;
import com.gdb.domain.*;
import com.gdb.exceptions.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-backed implementation of AccountRepository.
 * Manages database persistence for accounts using PreparedStatements and ConnectionProvider.
 */
public class JdbcAccountRepository implements AccountRepository {

    private final ConnectionProvider provider;

    public JdbcAccountRepository(ConnectionProvider provider) {
        this.provider = provider;
    }

    @Override
    public void save(IAccount account) {
        if (account == null) return;
        String sql = "INSERT INTO accounts (account_number, account_holder_name, age, balance, account_type, "
                + "status, pin, opening_date, overdraft_used, interest_earned) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            Account acc = (Account) account;
            ps.setInt(1, acc.getAccountNumber());
            ps.setString(2, acc.getAccountHolderName());
            ps.setInt(3, acc.getAge());
            ps.setDouble(4, acc.getBalance());
            ps.setString(5, acc.getAccountType());
            ps.setString(6, acc.isActive() ? "Active" : "Inactive");
            if (acc.getPin() == null) {
                ps.setNull(7, Types.INTEGER);
            } else {
                ps.setInt(7, acc.getPin());
            }
            ps.setString(8, acc.getOpeningDate());
            ps.setDouble(9, overdraftUsedOf(acc));
            ps.setDouble(10, interestEarnedOf(acc));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not save account " + account.getAccountNumber(), e);
        }
    }

    @Override
    public IAccount findById(int accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not find account " + accountNumber, e);
        }
    }

    @Override
    public List<IAccount> findAll() {
        List<IAccount> accounts = new ArrayList<>();
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM accounts");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                accounts.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load accounts", e);
        }
        return accounts;
    }

    @Override
    public void update(IAccount account) {
        if (account == null) return;
        String sql = "UPDATE accounts SET balance = ?, status = ?, overdraft_used = ?, interest_earned = ? "
                + "WHERE account_number = ?";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, account.getBalance());
            ps.setString(2, account.isActive() ? "Active" : "Inactive");
            ps.setDouble(3, overdraftUsedOf(account));
            ps.setDouble(4, interestEarnedOf(account));
            ps.setInt(5, account.getAccountNumber());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not update account " + account.getAccountNumber(), e);
        }
    }

    @Override
    public void delete(int accountNumber) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not delete account " + accountNumber, e);
        }
    }

    @Override
    public boolean exists(int accountNumber) {
        String sql = "SELECT 1 FROM accounts WHERE account_number = ?";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not check account " + accountNumber, e);
        }
    }

    @Override
    public int nextAccountNumber() {
        String sql = "SELECT COALESCE(MAX(account_number), 1000) + 1 AS next_id FROM accounts";
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("next_id") : 1001;
        } catch (SQLException e) {
            throw new DataAccessException("Could not generate next account number", e);
        }
    }

    // Current accounts track overdraft and fixed deposits track interest, everything else stores 0
    private double overdraftUsedOf(IAccount acc) {
        return acc instanceof CurrentAccount ? ((CurrentAccount) acc).getOverdraftUsed() : 0.0;
    }

    private double interestEarnedOf(IAccount acc) {
        return acc instanceof FixedDepositAccount ? ((FixedDepositAccount) acc).getInterestEarned() : 0.0;
    }

    public IAccount mapRow(ResultSet rs) throws SQLException {
        int accNo = rs.getInt("account_number");
        String name = rs.getString("account_holder_name");
        int age = rs.getInt("age");
        double balance = rs.getDouble("balance");
        String type = rs.getString("account_type");
        String status = rs.getString("status");
        int pin = rs.getInt("pin");

        try {
            Account acc = (Account) AccountFactory.createAccount(type, accNo, name, Math.max(18, age), balance);
            if (pin > 0) {
                try { acc.setPin(pin); } catch (Exception ignored) {}
            }
            if (!"Active".equalsIgnoreCase(status)) {
                try { acc.closeAccount(); } catch (Exception ignored) {}
            }
            return acc;
        } catch (Exception e) {
            throw new SQLException("Error mapping row to Account: " + e.getMessage(), e);
        }
    }
}

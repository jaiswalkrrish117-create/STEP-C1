package com.gdb.repository;

import com.gdb.db.ConnectionProvider;
import com.gdb.domain.Transaction;
import com.gdb.domain.TransactionType;
import com.gdb.exceptions.DataAccessException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC-backed implementation of TransactionRepository.
 * Handles transaction record persistence, retrieval, and multi-account atomic transfers.
 */
public class JdbcTransactionRepository implements TransactionRepository {

    private final ConnectionProvider provider;

    public JdbcTransactionRepository(ConnectionProvider provider) {
        this.provider = provider;
    }

    @Override
    public void save(Transaction transaction) {
        if (transaction == null) return;
        try (Connection conn = provider.getConnection()) {
            insertTransaction(conn, transaction);
        } catch (SQLException e) {
            throw new DataAccessException("Could not save transaction " + transaction.getTransactionId(), e);
        }
    }

    @Override
    public List<Transaction> findByAccount(int accountNumber) {
        String sql = "SELECT * FROM transactions WHERE account_number = ? OR from_account = ? OR to_account = ? "
                + "ORDER BY timestamp ASC";
        List<Transaction> result = new ArrayList<>();
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountNumber);
            ps.setInt(2, accountNumber);
            ps.setInt(3, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load transactions for account " + accountNumber, e);
        }
        return result;
    }

    @Override
    public List<Transaction> findAll() {
        List<Transaction> result = new ArrayList<>();
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM transactions ORDER BY timestamp ASC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not load transactions", e);
        }
        return result;
    }

    @Override
    public void clear() {
        try (Connection conn = provider.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM transactions")) {
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Could not clear transactions", e);
        }
    }

    @Override
    public void transfer(int fromAccountNumber, int toAccountNumber, double amount) {
        Connection conn = null;
        try {
            conn = provider.getConnection();
            // everything below shares this one connection and commits (or rolls back) together
            conn.setAutoCommit(false);

            double senderBalance;
            try (PreparedStatement ps = conn.prepareStatement("SELECT balance FROM accounts WHERE account_number = ?")) {
                ps.setInt(1, fromAccountNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Sender account not found: " + fromAccountNumber);
                    }
                    senderBalance = rs.getDouble("balance");
                }
            }
            if (senderBalance < amount) {
                throw new DataAccessException("Insufficient balance");
            }

            try (PreparedStatement debit = conn.prepareStatement(
                    "UPDATE accounts SET balance = balance - ? WHERE account_number = ?")) {
                debit.setDouble(1, amount);
                debit.setInt(2, fromAccountNumber);
                debit.executeUpdate();
            }
            try (PreparedStatement credit = conn.prepareStatement(
                    "UPDATE accounts SET balance = balance + ? WHERE account_number = ?")) {
                credit.setDouble(1, amount);
                credit.setInt(2, toAccountNumber);
                if (credit.executeUpdate() == 0) {
                    throw new DataAccessException("Receiver account not found: " + toAccountNumber);
                }
            }

            Transaction audit = new Transaction(
                    Transaction.generateId(), LocalDateTime.now(), fromAccountNumber, TransactionType.TRANSFER,
                    amount, senderBalance - amount, "SUCCESS",
                    "Transfer of Rs. " + amount + " to Account #" + toAccountNumber,
                    fromAccountNumber, toAccountNumber);
            insertTransaction(conn, audit);

            conn.commit();
        } catch (SQLException | RuntimeException e) {
            rollbackQuietly(conn);
            if (e instanceof DataAccessException) {
                throw (DataAccessException) e;
            }
            throw new DataAccessException("Transfer failed and was rolled back: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    // writes one row using whatever connection it is given, so transfer() can keep it in its own transaction
    private void insertTransaction(Connection conn, Transaction t) throws SQLException {
        String sql = "INSERT INTO transactions (transaction_id, timestamp, account_number, type, amount, "
                + "balance_after, status, from_account, to_account, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, t.getTransactionId());
            ps.setString(2, t.getTimestamp().toString());
            ps.setInt(3, t.getAccountNumber());
            ps.setString(4, t.getType().name());
            ps.setDouble(5, t.getAmount());
            ps.setDouble(6, t.getBalanceAfter());
            ps.setString(7, t.getStatus());
            ps.setInt(8, t.getFromAccount());
            ps.setInt(9, t.getToAccount());
            ps.setString(10, t.getDescription());
            ps.executeUpdate();
        }
    }

    private void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ignored) {
            }
        }
    }

    private Transaction mapRow(ResultSet rs) throws SQLException {
        String id = rs.getString("transaction_id");
        String tsStr = rs.getString("timestamp");
        LocalDateTime ts = tsStr != null ? LocalDateTime.parse(tsStr) : LocalDateTime.now();
        int accNum = rs.getInt("account_number");
        String typeStr = rs.getString("type");
        TransactionType type = TransactionType.valueOf(typeStr);
        double amt = rs.getDouble("amount");
        double balanceAfter = rs.getDouble("balance_after");
        String status = rs.getString("status");
        String remarks = rs.getString("description");
        int fromAcc = rs.getInt("from_account");
        int toAcc = rs.getInt("to_account");

        return new Transaction(id, ts, accNum, type, amt, balanceAfter, status, remarks, fromAcc, toAcc);
    }
}

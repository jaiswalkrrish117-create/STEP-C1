package com.gdb.tests;

import com.gdb.db.ConnectionProvider;
import com.gdb.db.JdbcConnectionProvider;
import com.gdb.db.SchemaInitializer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestJdbcConnection {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 22 — JDBC FOUNDATION (CONNECTION & SCHEMA)");
        System.out.println("=".repeat(60));

        String testDbUrl = "jdbc:sqlite:test_gdb.db";
        ConnectionProvider provider = new JdbcConnectionProvider(testDbUrl);

        System.out.println("[TEST 1] Database Connection Establishment:");
        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            assert rs.next() && rs.getInt(1) == 1 : "SELECT 1 should return 1";
            System.out.println("  Connected to: " + testDbUrl);
            System.out.println("  Driver Name: " + conn.getMetaData().getDriverName());
            System.out.println("  -> PASSED");
        }

        System.out.println("\n[TEST 2] Schema DDL Execution:");
        System.out.println("  Creating tables: accounts, transactions...");
        SchemaInitializer.initialize(provider);
        System.out.println("  Tables initialized successfully.");
        System.out.println("  -> PASSED");

        System.out.println("\n[TEST 3] Schema Verification:");
        boolean accountsExists = tableExists(provider, "accounts");
        boolean transactionsExists = tableExists(provider, "transactions");
        System.out.println("  Table 'accounts' exists: " + accountsExists);
        System.out.println("  Table 'transactions' exists: " + transactionsExists);
        assert accountsExists && transactionsExists : "both tables must exist after initialize";
        System.out.println("  -> PASSED");

        provider.shutdown();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ALL ACTIVITY 22 JDBC CONNECTION TESTS PASSED!");
        System.out.println("=".repeat(60));
        System.out.println("Activity 22 JDBC tests complete.");
    }

    // asks sqlite_master whether a table with this name was created
    private static boolean tableExists(ConnectionProvider provider, String name) throws Exception {
        try (Connection conn = provider.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement(
                     "SELECT name FROM sqlite_master WHERE type='table' AND name=?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}

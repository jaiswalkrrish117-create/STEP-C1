package com.gdb.tests;

import com.gdb.db.*;
import com.gdb.domain.*;
import com.gdb.repository.*;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

public class TestConnectionPooling {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 25 — CONNECTION POOLING WITH HIKARICP (NFR)");
        System.out.println("=".repeat(60));

        Properties props = new Properties();
        props.setProperty("persistence.db.url", "jdbc:sqlite:test_pool_gdb.db");
        props.setProperty("persistence.db.driver", "org.sqlite.JDBC");
        props.setProperty("persistence.pool.enabled", "true");
        props.setProperty("persistence.pool.size", "5");
        props.setProperty("persistence.pool.timeout", "10000");

        File dbFile = new File("test_pool_gdb.db");
        if (dbFile.exists()) dbFile.delete();

        System.out.println("[TEST 1] HikariCP Connection Pool Initialization:");
        HikariConnectionProvider pooled = new HikariConnectionProvider(props);
        SchemaInitializer.initialize(pooled);
        System.out.println("  Pool Name: " + pooled.getDataSource().getPoolName());
        System.out.println("  Max Pool Size: " + pooled.getDataSource().getMaximumPoolSize());
        assert pooled.getDataSource().getMaximumPoolSize() == 5 : "pool size should come from the properties";
        System.out.println("  -> PASSED");

        System.out.println("\n[TEST 2] Connection Borrowing & Returning:");
        try (Connection conn = pooled.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1")) {
            boolean got = rs.next() && rs.getInt(1) == 1;
            System.out.println("  Connection acquired: " + got);
            assert got : "pooled connection should run SELECT 1";
        }
        // the connection was closed above, so it is back in the pool and can be borrowed again
        try (Connection again = pooled.getConnection()) {
            System.out.println("  Connection returned to pool and borrowed again: " + !again.isClosed());
        }
        System.out.println("  -> PASSED");

        System.out.println("\n[TEST 3] Latency Benchmark (50 sequential connection cycles):");
        int runs = 50;
        ConnectionProvider direct = new JdbcConnectionProvider(props.getProperty("persistence.db.url"));
        long directMs = timeQueries(direct, runs);
        long pooledMs = timeQueries(pooled, runs);
        System.out.println("  Unpooled Direct Connections: " + directMs + " ms (avg "
                + String.format("%.2f", directMs / (double) runs) + " ms/op)");
        System.out.println("  HikariCP Pooled Connections:  " + pooledMs + " ms (avg "
                + String.format("%.2f", pooledMs / (double) runs) + " ms/op)");
        System.out.println("  -> PASSED");

        System.out.println("\n[TEST 4] Repository Operations over HikariCP:");
        AccountRepository repo = new JdbcAccountRepository(pooled);
        IAccount acc = AccountFactory.createAccount("SAVINGS", 1001, "Rajesh Sharma", 30, 50000.0);
        acc.setPin(1234);
        repo.save(acc);
        System.out.println("  Saved Account #1001 via pooled repository.");
        IAccount loaded = repo.findById(1001);
        assert loaded != null && loaded.getBalance() == 50000.0 : "account should round-trip through the pool";
        System.out.println("  Retrieved Account #1001: Balance Rs. " + loaded.getBalance());
        System.out.println("  -> PASSED");

        pooled.shutdown();
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  ALL ACTIVITY 25 CONNECTION POOLING TESTS PASSED!");
        System.out.println("=".repeat(60));

        if (dbFile.exists()) dbFile.delete();
        System.out.println("Activity 25 tests complete.");
    }

    // runs a trivial query `runs` times, borrowing a fresh connection each time, and returns the total ms
    private static long timeQueries(ConnectionProvider provider, int runs) throws Exception {
        long start = System.nanoTime();
        for (int i = 0; i < runs; i++) {
            try (Connection conn = provider.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {
                rs.next();
            }
        }
        return (System.nanoTime() - start) / 1_000_000;
    }
}

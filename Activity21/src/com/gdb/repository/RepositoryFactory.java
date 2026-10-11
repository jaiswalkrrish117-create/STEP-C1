package com.gdb.repository;

import java.io.*;
import java.util.Properties;

/**
 * Factory creating and supplying repository instances based on persistence.properties configuration.
 */
public class RepositoryFactory {

    private static AccountRepository accountRepositoryInstance;
    private static TransactionRepository transactionRepositoryInstance;

    public static String getPersistenceMode() {
        Properties props = new Properties();
        // classpath first, then a plain file next to the program
        try (InputStream in = openConfig()) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read persistence.properties, using memory mode");
        }
        return props.getProperty("persistence.mode", "memory").trim().toLowerCase();
    }

    private static InputStream openConfig() throws IOException {
        InputStream in = RepositoryFactory.class.getClassLoader()
                .getResourceAsStream("config/persistence.properties");
        if (in == null) {
            File file = new File("config/persistence.properties");
            if (file.exists()) {
                in = new FileInputStream(file);
            }
        }
        return in;
    }

    public static synchronized AccountRepository getAccountRepository() {
        String mode = getPersistenceMode();
        switch (mode) {
            case "jdbc":
                throw new UnsupportedOperationException("JDBC repository not implemented yet - coming in Activity 22");
            case "file":
                throw new UnsupportedOperationException("File repository not implemented yet");
            default:
                if (accountRepositoryInstance == null) {
                    accountRepositoryInstance = new InMemoryAccountRepository();
                }
                return accountRepositoryInstance;
        }
    }

    public static synchronized TransactionRepository getTransactionRepository() {
        String mode = getPersistenceMode();
        switch (mode) {
            case "jdbc":
                throw new UnsupportedOperationException("JDBC repository not implemented yet - coming in Activity 22");
            case "file":
                throw new UnsupportedOperationException("File repository not implemented yet");
            default:
                if (transactionRepositoryInstance == null) {
                    transactionRepositoryInstance = new InMemoryTransactionRepository();
                }
                return transactionRepositoryInstance;
        }
    }
}

package com.gdb.db;

import java.io.*;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Initializes the database schema idempotently by executing schema.sql DDL statements.
 */
public class SchemaInitializer {

    public static void initialize(ConnectionProvider provider) {
        String script = readSchemaSql();
        if (script.isEmpty()) {
            throw new IllegalStateException("schema.sql could not be found or is empty");
        }
        try (Connection conn = provider.getConnection();
             Statement stmt = conn.createStatement()) {
            // the script holds several statements separated by ';'
            for (String sql : script.split(";")) {
                if (!sql.trim().isEmpty()) {
                    stmt.execute(sql.trim());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not initialize schema: " + e.getMessage(), e);
        }
    }

    public static String readSchemaSql() {
        // classpath first (bin/schema.sql), then the source folder
        try (InputStream in = SchemaInitializer.class.getClassLoader().getResourceAsStream("schema.sql")) {
            if (in != null) {
                return new String(in.readAllBytes());
            }
        } catch (IOException e) {
            System.out.println("Warning: could not read schema.sql from classpath: " + e.getMessage());
        }
        File file = new File("src/main/resources/schema.sql");
        if (file.exists()) {
            try (InputStream in = new FileInputStream(file)) {
                return new String(in.readAllBytes());
            } catch (IOException e) {
                System.out.println("Warning: could not read schema.sql file: " + e.getMessage());
            }
        }
        return "";
    }
}

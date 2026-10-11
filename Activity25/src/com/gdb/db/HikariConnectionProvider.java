package com.gdb.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Production-grade ConnectionProvider backed by HikariCP Connection Pool.
 */
public class HikariConnectionProvider implements ConnectionProvider {

    private HikariDataSource dataSource;

    public HikariConnectionProvider(Properties config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getProperty("persistence.db.url", "jdbc:sqlite:gdb.db"));
        hikariConfig.setDriverClassName(config.getProperty("persistence.db.driver", "org.sqlite.JDBC"));
        hikariConfig.setMaximumPoolSize(Integer.parseInt(config.getProperty("persistence.pool.size", "10").trim()));
        hikariConfig.setConnectionTimeout(Long.parseLong(config.getProperty("persistence.pool.timeout", "30000").trim()));
        this.dataSource = new HikariDataSource(hikariConfig);
    }

    @Override
    public Connection getConnection() throws SQLException {
        // hands out a connection from the pool, closing it returns it to the pool
        return dataSource.getConnection();
    }

    @Override
    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    @Override
    public String getProviderName() {
        return "HikariConnectionProvider [Pool size: " + (dataSource != null ? dataSource.getMaximumPoolSize() : "N/A") + "]";
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}

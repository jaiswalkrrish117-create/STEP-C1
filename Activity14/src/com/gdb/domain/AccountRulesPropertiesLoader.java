package com.gdb.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class AccountRulesPropertiesLoader {
    private Properties properties = new Properties();

    public AccountRulesPropertiesLoader(String configPath) {
        loadProperties(configPath);
    }

    private void loadProperties(String configPath) {
        // look on the classpath first, then fall back to a normal file path
        InputStream in = getClass().getClassLoader().getResourceAsStream(configPath);
        try {
            if (in == null) {
                File file = new File(configPath);
                if (file.exists()) {
                    in = new FileInputStream(file);
                }
            }
            if (in != null) {
                properties.load(in);
            } else {
                System.out.println("Warning: could not find rules file " + configPath);
            }
        } catch (Exception e) {
            System.out.println("Warning: could not read rules file " + configPath + " (" + e.getMessage() + ")");
        } finally {
            if (in != null) {
                try { in.close(); } catch (Exception ignored) { }
            }
        }
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public double getDouble(String key, double defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

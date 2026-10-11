package com.gdb.file;

import com.gdb.domain.*;
import com.gdb.exceptions.AccountException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility mapping between Domain models and CSV formatted rows.
 */
public class CsvMapper {

    public static final String ACCOUNT_HEADER = "account_number,account_holder_name,age,balance,account_type,status,pin,opening_date,overdraft_used,interest_earned";
    public static final String TRANSACTION_HEADER = "transaction_id,timestamp,account_number,type,amount,balance_after,status,from_account,to_account,description";

    public static String toCsv(IAccount account) {
        Account acc = (Account) account;
        double overdraftUsed = acc instanceof CurrentAccount ? ((CurrentAccount) acc).getOverdraftUsed() : 0.0;
        double interestEarned = acc instanceof FixedDepositAccount ? ((FixedDepositAccount) acc).getInterestEarned() : 0.0;
        return String.join(",",
                String.valueOf(acc.getAccountNumber()),
                escape(acc.getAccountHolderName()),
                String.valueOf(acc.getAge()),
                String.valueOf(acc.getBalance()),
                escape(acc.getAccountType()),
                acc.isActive() ? "Active" : "Inactive",
                acc.getPin() == null ? "" : String.valueOf(acc.getPin()),
                escape(acc.getOpeningDate()),
                String.valueOf(overdraftUsed),
                String.valueOf(interestEarned));
    }

    public static IAccount fromCsv(String line) {
        List<String> f = split(line);
        if (f.size() < 8) {
            throw new IllegalArgumentException("Account row needs at least 8 columns: " + line);
        }
        try {
            int accNo = Integer.parseInt(f.get(0).trim());
            String name = f.get(1);
            int age = Integer.parseInt(f.get(2).trim());
            double balance = Double.parseDouble(f.get(3).trim());
            // the file holds the display name (Savings, Fixed Deposit...), the factory wants letters only
            String type = f.get(4).replaceAll("[^A-Za-z]", "");
            String status = f.get(5).trim();
            String pin = f.get(6).trim();

            Account acc = (Account) AccountFactory.createAccount(type, accNo, name, age, balance);
            if (!pin.isEmpty() && Integer.parseInt(pin) > 0) {
                acc.setPin(Integer.parseInt(pin));
            }
            if (!"Active".equalsIgnoreCase(status)) {
                acc.closeAccount();
            }
            return acc;
        } catch (AccountException | NumberFormatException e) {
            throw new IllegalArgumentException("Bad account row (" + e.getMessage() + "): " + line, e);
        }
    }

    public static String toCsv(Transaction t) {
        return String.join(",",
                escape(t.getTransactionId()),
                t.getTimestamp().toString(),
                String.valueOf(t.getAccountNumber()),
                t.getType().name(),
                String.valueOf(t.getAmount()),
                String.valueOf(t.getBalanceAfter()),
                escape(t.getStatus()),
                String.valueOf(t.getFromAccount()),
                String.valueOf(t.getToAccount()),
                escape(t.getDescription()));
    }

    public static Transaction fromCsvTransaction(String line) {
        List<String> f = split(line);
        if (f.size() < 10) {
            throw new IllegalArgumentException("Transaction row needs 10 columns: " + line);
        }
        try {
            return new Transaction(
                    f.get(0),
                    LocalDateTime.parse(f.get(1).trim()),
                    Integer.parseInt(f.get(2).trim()),
                    TransactionType.valueOf(f.get(3).trim()),
                    Double.parseDouble(f.get(4).trim()),
                    Double.parseDouble(f.get(5).trim()),
                    f.get(6),
                    f.get(9),
                    Integer.parseInt(f.get(7).trim()),
                    Integer.parseInt(f.get(8).trim()));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Bad transaction row (" + e.getMessage() + "): " + line, e);
        }
    }

    // returns the same account row with only the balance column replaced (used by file transfers)
    public static String withBalance(String accountRow, double newBalance) {
        List<String> fields = split(accountRow);
        fields.set(3, String.valueOf(newBalance));
        List<String> out = new ArrayList<>();
        for (String field : fields) {
            out.add(escape(field));
        }
        return String.join(",", out);
    }

    // a value with a comma or quote is wrapped in quotes, and inner quotes are doubled
    private static String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // splits one CSV row, understanding quoted fields
    public static List<String> split(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else if (c == '"') {
                    inQuotes = false;
                } else {
                    current.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields;
    }
}

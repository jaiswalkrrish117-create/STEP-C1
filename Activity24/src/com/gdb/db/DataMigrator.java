package com.gdb.db;

import com.gdb.command.TransactionCommand;
import com.gdb.domain.Transaction;
import com.gdb.logging.LogDestination;
import com.gdb.repository.TransactionRepository;

import java.util.List;

/**
 * Utility for migrating legacy file/in-memory transaction command logs into relational SQLite database.
 */
public class DataMigrator {

    public static int migrate(LogDestination source, TransactionRepository target) {
        int migrated = 0;
        for (TransactionCommand cmd : source.readAll()) {
            Transaction txn = cmd.getTransaction();
            if (txn != null) {
                target.save(txn);
                migrated++;
            }
        }
        return migrated;
    }
}

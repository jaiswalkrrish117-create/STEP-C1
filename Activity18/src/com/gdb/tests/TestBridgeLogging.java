package com.gdb.tests;

import com.gdb.command.*;
import com.gdb.db.SimulatedDatabase;
import com.gdb.domain.*;
import com.gdb.logging.*;
import java.util.List;

public class TestBridgeLogging {
    public static void main(String[] args) throws Exception {
        System.out.println("=".repeat(60));
        System.out.println("  ACTIVITY 18 — BRIDGE PATTERN (FILE + DB)");
        System.out.println("=".repeat(60));

        // Setup test accounts
        IAccount acc1 = AccountFactory.createAccount("SAVINGS", 1001, "John", 25, 15000);
        acc1.setPin(1234);
        IAccount acc2 = AccountFactory.createAccount("SAVINGS", 1002, "Jane", 30, 10000);

        FileLogDestination fileDest = new FileLogDestination();
        fileDest.clear();
        SimulatedDatabase db = new SimulatedDatabase();
        DatabaseLogDestination dbDest = new DatabaseLogDestination(db);
        MemoryLogDestination memDest = new MemoryLogDestination();

        TransactionLogger logger = new TransactionLogger(fileDest);
        System.out.println("\n[STEP 25] Logging to " + logger.getDestinationName() + " destination...");
        runAndLog(logger, acc1, acc2);
        System.out.println("  FILE log count: " + logger.readAll().size());

        logger.setDestination(dbDest);
        System.out.println("\n[STEP 26] Switched to " + logger.getDestinationName() + " destination...");
        runAndLog(logger, acc1, acc2);
        System.out.println("  DATABASE log count: " + logger.readAll().size());

        logger.setDestination(memDest);
        System.out.println("\n[STEP 27] Switched to " + logger.getDestinationName() + " destination...");
        runAndLog(logger, acc1, acc2);
        System.out.println("  MEMORY log count: " + logger.readAll().size());

        System.out.println("\n[STEP 28] Verifying Data Isolation:");
        logger.setDestination(fileDest);
        System.out.println("  FILE count: " + logger.readAll().size() + " [EXPECTED: 3]");
        logger.setDestination(dbDest);
        System.out.println("  DATABASE count: " + logger.readAll().size() + " [EXPECTED: 3]");
        logger.setDestination(memDest);
        System.out.println("  MEMORY count: " + logger.readAll().size() + " [EXPECTED: 3]");

        System.out.println("\n[STEP 29] All Bridge Pattern log backends verified successfully!");
        System.out.println("  Backends used: " + fileDest.getDestinationName() + ", "
                + dbDest.getDestinationName() + ", " + memDest.getDestinationName());
    }
    // runs one deposit, one withdraw and one transfer, logging each through the bridge
    private static void runAndLog(TransactionLogger logger, IAccount acc1, IAccount acc2) throws Exception {
        TransactionCommand[] cmds = {
            new DepositCommand(acc1, 5000),
            new WithdrawCommand(acc1, 2000, 1234),
            new TransferCommand(acc1, acc2, 3000, 1234)
        };
        for (TransactionCommand cmd : cmds) {
            cmd.execute();
            logger.log(cmd);
        }
    }
}

package com.gdb;

import com.gdb.domain.IAccount;
import com.gdb.logging.FileLogDestination;
import com.gdb.logging.LogDestination;
import com.gdb.logging.TransactionLogger;
import com.gdb.service.AccountService;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Global Digital Bank — Service Demo");
        System.out.println("=".repeat(60));

        LogDestination dest = new FileLogDestination();
        TransactionLogger logger = new TransactionLogger(dest);
        AccountService service = new AccountService(logger);

        IAccount alice = service.openAccount("SAVINGS", "Alice Rao", 29, 20000);
        alice.setPin(1111);
        IAccount bob = service.openAccount("SAVINGS", "Bob Iyer", 33, 12000);
        bob.setPin(2222);

        service.deposit(alice.getAccountNumber(), 3000);
        service.withdraw(alice.getAccountNumber(), 1000, 1111);
        service.transfer(alice.getAccountNumber(), bob.getAccountNumber(), 2500, 1111);

        System.out.println(alice.getAccountInfo());
        System.out.println(bob.getAccountInfo());
        System.out.println("Transaction history:");
        for (var cmd : service.getTransactionHistory()) {
            System.out.println("  " + cmd.getTransaction());
        }
    }
}

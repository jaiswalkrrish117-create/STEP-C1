package com.gdb.logging;

import com.gdb.command.TransactionCommand;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileLogDestination implements LogDestination {
    private TransactionLog log;

    public FileLogDestination() {
        this.log = new TransactionLog();
    }

    @Override
    public void write(TransactionCommand cmd) {
        try {
            log.log(cmd);
        } catch (IOException e) {
            System.out.println("Could not write to file log: " + e.getMessage());
        }
    }

    @Override
    public List<TransactionCommand> readAll() {
        try {
            return log.readAll();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not read file log: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public void clear() {
        log.clear();
    }

    @Override
    public String getDestinationName() {
        return "FILE";
    }
}

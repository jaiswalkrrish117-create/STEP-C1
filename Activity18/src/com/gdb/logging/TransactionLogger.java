package com.gdb.logging;

import com.gdb.command.TransactionCommand;
import java.util.List;

/**
 * Bridge abstraction class decoupling high-level logger API from pluggable storage backends.
 */
public class TransactionLogger {
    protected LogDestination destination;

    public TransactionLogger(LogDestination destination) {
        this.destination = destination;
    }

    // lets the caller switch the storage backend at runtime
    public void setDestination(LogDestination destination) {
        this.destination = destination;
    }

    public void log(TransactionCommand cmd) {
        destination.write(cmd);
    }

    public List<TransactionCommand> readAll() {
        return destination.readAll();
    }

    public void clear() {
        destination.clear();
    }

    public String getDestinationName() {
        return destination.getDestinationName();
    }
}

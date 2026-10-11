package com.gdb.logging;

import com.gdb.command.TransactionCommand;
import java.io.*;
import java.util.*;

public class TransactionLog {
    private static final String FILE_PATH = "data/transactions.ser";

    // Appending to an existing file must not write a second stream header,
    // otherwise ObjectInputStream fails when it reaches it.
    private static class AppendableObjectOutputStream extends ObjectOutputStream {
        public AppendableObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }
        @Override
        protected void writeStreamHeader() throws IOException {
            // do not write header when appending to an existing stream
        }
    }

    public synchronized void log(TransactionCommand cmd) throws IOException {
        File file = new File(FILE_PATH);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        boolean fresh = !file.exists() || file.length() == 0;
        try (ObjectOutputStream out = fresh
                ? new ObjectOutputStream(new FileOutputStream(file))
                : new AppendableObjectOutputStream(new FileOutputStream(file, true))) {
            out.writeObject(cmd);
            out.flush();
        }
    }

    public synchronized List<TransactionCommand> readAll() throws IOException, ClassNotFoundException {
        List<TransactionCommand> commands = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return commands;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    commands.add((TransactionCommand) in.readObject());
                } catch (EOFException e) {
                    break; // reached the end of the log
                }
            }
        }
        return commands;
    }

    public synchronized void clear() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }
}

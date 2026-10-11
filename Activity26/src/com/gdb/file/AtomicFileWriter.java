package com.gdb.file;

import java.io.*;
import java.nio.file.*;
import java.util.List;

/**
 * Provides atomic, crash-resilient file write operations using temp files and atomic rename.
 */
public class AtomicFileWriter {

    public static void write(String path, List<String> lines) throws IOException {
        Path target = Paths.get(path);
        Path parent = target.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

        // write everything to a temp file first, so the real file is never half written
        Path temp = Paths.get(path + ".tmp");
        try (BufferedWriter writer = Files.newBufferedWriter(temp)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
        }

        try {
            Files.move(temp, target, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            // some file systems cannot do an atomic move, a plain replace is the next best thing
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}

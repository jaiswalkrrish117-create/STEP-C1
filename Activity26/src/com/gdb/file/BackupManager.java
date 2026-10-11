package com.gdb.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Manages timestamped backups of data files and maintains retention limits.
 */
public class BackupManager {

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    public static void backup(String path, int maxBackups) throws IOException {
        File source = new File(path);
        if (maxBackups <= 0 || !source.exists()) {
            return;
        }

        String prefix = source.getName() + ".bak.";
        File dir = source.getAbsoluteFile().getParentFile();

        // two backups in the same millisecond must not overwrite each other
        String stamp = LocalDateTime.now().format(STAMP);
        File backupFile = new File(dir, prefix + stamp);
        int counter = 1;
        while (backupFile.exists()) {
            backupFile = new File(dir, prefix + stamp + "_" + counter++);
        }
        Files.copy(source.toPath(), backupFile.toPath());

        // keep only the newest maxBackups files, the timestamp in the name sorts oldest first
        List<File> backups = listBackups(path);
        int excess = backups.size() - maxBackups;
        for (int i = 0; i < excess; i++) {
            backups.get(i).delete();
        }
    }

    // all backup files of one data file, oldest first
    public static List<File> listBackups(String path) {
        File source = new File(path);
        String prefix = source.getName() + ".bak.";
        File[] found = source.getAbsoluteFile().getParentFile()
                .listFiles((d, name) -> name.startsWith(prefix));
        List<File> backups = new ArrayList<>();
        if (found != null) {
            backups.addAll(Arrays.asList(found));
        }
        backups.sort(Comparator.comparing(File::getName));
        return backups;
    }
}

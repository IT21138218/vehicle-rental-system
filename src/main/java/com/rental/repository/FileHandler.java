package com.rental.repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Reads and writes the lines of ONE text file. This is the only class in the
 * project that touches the disk directly.
 *
 * <ul>
 *   <li>Missing files (and folders) are created automatically.</li>
 *   <li>Blank lines are skipped when reading.</li>
 *   <li>Writes are safe: the new content goes into a temporary file first and then
 *       replaces the real file in one step, so a crash never leaves a half-written file.</li>
 *   <li>Methods are {@code synchronized}, so two browser requests cannot write at the same time.</li>
 * </ul>
 *
 * <p><b>OOP concept - Information hiding + Encapsulation:</b> the file path is private and
 * callers only see "give me the lines" / "save these lines".</p>
 */
public class FileHandler {

    private static final Logger LOG = Logger.getLogger(FileHandler.class.getName());

    private final Path filePath;

    /**
     * Creates a handler for one data file.
     *
     * @param filePath full path of the text file, e.g. data/vehicles.txt
     */
    public FileHandler(Path filePath) {
        if (filePath == null) {
            throw new IllegalArgumentException("File path is required");
        }
        this.filePath = filePath;
    }

    /**
     * Returns the file name (used in log messages).
     *
     * @return the file name, e.g. "vehicles.txt"
     */
    public String getFileName() {
        return filePath.getFileName().toString();
    }

    /**
     * Reads every non-blank line of the file.
     *
     * @return the lines in file order (blank lines removed)
     * @throws UncheckedIOException if the file cannot be read
     */
    public synchronized List<String> readLines() {
        ensureFileExists();
        try {
            List<String> result = new ArrayList<>();
            for (String line : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!line.isBlank()) {
                    result.add(line.trim());
                }
            }
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read " + filePath, e);
        }
    }

    /**
     * Replaces the whole file with the given lines, safely.
     *
     * <ol>
     *   <li>Write all lines to "name.txt.tmp".</li>
     *   <li>Move the temp file over the real file (atomic where the OS supports it).</li>
     * </ol>
     *
     * @param lines the lines to save, one record per line
     * @throws UncheckedIOException if the file cannot be written
     */
    public synchronized void writeLines(List<String> lines) {
        ensureFileExists();
        Path tempFile = filePath.resolveSibling(getFileName() + ".tmp");
        try {
            Files.write(tempFile, lines, StandardCharsets.UTF_8);
            try {
                Files.move(tempFile, filePath,
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not write " + filePath, e);
        }
    }

    /** Creates the folder and an empty file if they do not exist yet. */
    private void ensureFileExists() {
        try {
            if (Files.notExists(filePath)) {
                Files.createDirectories(filePath.toAbsolutePath().getParent());
                Files.createFile(filePath);
                LOG.info("Created missing data file " + filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create " + filePath, e);
        }
    }
}

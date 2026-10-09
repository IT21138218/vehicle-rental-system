package com.rental.repository;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

/**
 * Decides which folder holds the data files.
 *
 * <ol>
 *   <li>If the JVM option {@code -Drental.data.dir=...} is set, that folder is used
 *       (recommended: point it at the project's data/ folder in the IntelliJ Tomcat config).</li>
 *   <li>Otherwise the folder {@code <user home>/vehicle-rental-data} is used.</li>
 * </ol>
 *
 * <p>{@link #seedIfMissing(Path)} copies the sample files into the data folder the first
 * time, so the demo has data straight away. Existing files are never overwritten.</p>
 *
 * <p><b>OOP concept - Information hiding:</b> file locations are known only to the
 * repository layer.</p>
 */
public final class DataPaths {

    /** Name of the JVM system property that overrides the data folder. */
    public static final String DATA_DIR_PROPERTY = "rental.data.dir";

    private static final Logger LOG = Logger.getLogger(DataPaths.class.getName());

    private DataPaths() {
    }

    /**
     * Returns the folder that holds all data files.
     *
     * @return the absolute data folder path
     */
    public static Path getDataDir() {
        String configured = System.getProperty(DATA_DIR_PROPERTY);
        if (configured != null && !configured.isBlank()) {
            return Paths.get(configured.trim()).toAbsolutePath();
        }
        return Paths.get(System.getProperty("user.home"), "vehicle-rental-data").toAbsolutePath();
    }

    /**
     * Returns the full path of one data file, e.g. {@code file("vehicles.txt")}.
     *
     * @param fileName the file name inside the data folder
     * @return the absolute path of that file
     */
    public static Path file(String fileName) {
        return getDataDir().resolve(fileName);
    }

    /**
     * Copies each sample *.txt file from {@code seedDir} into the data folder,
     * but only when the data folder does not have that file yet.
     *
     * @param seedDir folder containing the bundled sample files (may be null)
     */
    public static void seedIfMissing(Path seedDir) {
        if (seedDir == null || !Files.isDirectory(seedDir)) {
            LOG.warning("No sample data folder found; data files will start empty");
            return;
        }
        Path dataDir = getDataDir();
        try {
            Files.createDirectories(dataDir);
            try (DirectoryStream<Path> seedFiles = Files.newDirectoryStream(seedDir, "*.txt")) {
                for (Path seedFile : seedFiles) {
                    Path target = dataDir.resolve(seedFile.getFileName());
                    if (Files.notExists(target)) {
                        Files.copy(seedFile, target);
                        LOG.info("Copied sample data file to " + target);
                    }
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not prepare data folder " + dataDir, e);
        }
    }
}

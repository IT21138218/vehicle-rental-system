package com.rental.repository;

import com.rental.model.Storable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Implements the CRUD operations of {@link Repository} once, for every entity.
 *
 * <p>How it works: every operation loads all lines from the file, turns each line into an
 * object, changes the list in memory, and writes the whole list back. With a few hundred
 * records this is fast and very easy to understand.</p>
 *
 * <p>Subclasses (VehicleRepository, UserRepository, ...) only have to say how to turn ONE
 * line into ONE object by implementing {@link #parse(String[])}.</p>
 *
 * <p><b>OOP concepts:</b></p>
 * <ul>
 *   <li><b>Abstraction / Template Method:</b> the general steps are fixed here; the
 *       entity-specific step ({@code parse}) is abstract.</li>
 *   <li><b>Inheritance:</b> each concrete repository extends this class and reuses its code.</li>
 *   <li><b>Information hiding:</b> callers never see lines, commas or file paths.</li>
 * </ul>
 *
 * @param <T> the model type stored by this repository
 */
public abstract class AbstractFileRepository<T extends Storable> implements Repository<T> {

    private static final Logger LOG = Logger.getLogger(AbstractFileRepository.class.getName());

    private final FileHandler fileHandler;

    /**
     * @param filePath the data file this repository owns
     */
    protected AbstractFileRepository(Path filePath) {
        this.fileHandler = new FileHandler(filePath);
    }

    /**
     * Factory method: builds the correct object from the fields of one line.
     * For example VehicleRepository looks at fields[0] ("CAR", "BIKE" or "VAN")
     * to decide which subclass to create.
     *
     * @param fields the line split on commas
     * @return the object described by the line
     * @throws RuntimeException (e.g. IllegalArgumentException) if the line is malformed
     */
    protected abstract T parse(String[] fields);

    /**
     * How many pieces to split each line into. The default (-1) splits on every comma.
     * A repository whose LAST field is free text (like a review comment) overrides this,
     * so commas inside that text are kept.
     *
     * @return the limit passed to {@link String#split(String, int)}
     */
    protected int splitLimit() {
        return -1;
    }

    /**
     * {@inheritDoc}
     * Malformed lines are skipped and a warning is logged. Note: because every save
     * rewrites the file from this list, a skipped line disappears on the next save.
     */
    @Override
    public synchronized List<T> findAll() {
        List<T> items = new ArrayList<>();
        int recordNumber = 0;
        for (String line : fileHandler.readLines()) {
            recordNumber++;
            try {
                items.add(parse(line.split(Storable.SEPARATOR, splitLimit())));
            } catch (RuntimeException e) {
                LOG.warning("Skipping malformed record #" + recordNumber + " in "
                        + fileHandler.getFileName() + ": \"" + line + "\" (" + e.getMessage() + ")");
            }
        }
        return items;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized Optional<T> findById(String id) {
        for (T item : findAll()) {
            if (item.getId().equalsIgnoreCase(id)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    /** {@inheritDoc} */
    @Override
    public synchronized void add(T item) {
        List<T> items = findAll();
        for (T existing : items) {
            if (existing.getId().equalsIgnoreCase(item.getId())) {
                throw new IllegalArgumentException("A record with id " + item.getId() + " already exists");
            }
        }
        items.add(item);
        saveAll(items);
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean update(T item) {
        List<T> items = findAll();
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equalsIgnoreCase(item.getId())) {
                items.set(i, item);
                saveAll(items);
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public synchronized boolean delete(String id) {
        List<T> items = findAll();
        boolean removed = items.removeIf(item -> item.getId().equalsIgnoreCase(id));
        if (removed) {
            saveAll(items);
        }
        return removed;
    }

    /**
     * Writes every item back to the file, one {@code toFileString()} line each.
     *
     * <p><b>OOP concept - Polymorphism:</b> {@code item.toFileString()} runs the version
     * of the method that belongs to the real object (Car, Bike, Van ...).</p>
     */
    private void saveAll(List<T> items) {
        List<String> lines = new ArrayList<>();
        for (T item : items) {
            lines.add(item.toFileString());
        }
        fileHandler.writeLines(lines);
    }
}

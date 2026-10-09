package com.rental.repository;

import com.rental.model.Storable;

import java.util.List;
import java.util.Optional;

/**
 * The five basic CRUD operations every repository must support.
 *
 * <p><b>OOP concept - Abstraction + Generics:</b> the interface works for any type
 * {@code T} that is {@link Storable}. {@code Repository<Vehicle>}, {@code Repository<User>}
 * and so on all share the same method names, so services can use them the same way.</p>
 *
 * @param <T> the model type stored by this repository
 */
public interface Repository<T extends Storable> {

    /**
     * Saves a new record.
     *
     * @param item the record to save
     * @throws IllegalArgumentException if a record with the same id already exists
     */
    void add(T item);

    /**
     * Finds one record by its id.
     *
     * @param id the id to look for, e.g. "V001"
     * @return the record, or an empty Optional if no record has that id
     */
    Optional<T> findById(String id);

    /**
     * Returns every valid record in the file.
     *
     * @return a new list (changing it does not change the file)
     */
    List<T> findAll();

    /**
     * Replaces the stored record that has the same id as {@code item}.
     *
     * @param item the record with updated values
     * @return true if a record was found and updated, false if the id does not exist
     */
    boolean update(T item);

    /**
     * Removes the record with the given id.
     *
     * @param id the id of the record to remove
     * @return true if a record was removed, false if the id does not exist
     */
    boolean delete(String id);
}

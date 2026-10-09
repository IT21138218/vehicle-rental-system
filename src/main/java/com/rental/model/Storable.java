package com.rental.model;

/**
 * Anything that can be saved as one line in a text file.
 *
 * <p><b>OOP concept - Abstraction:</b> this interface says WHAT a storable record must
 * offer (an id and a file line) without saying HOW. Every model class (User, Vehicle,
 * Rental, Payment, Staff, Review) implements it, so the repository layer can save any of
 * them in the same way.</p>
 */
public interface Storable {

    /** Separator used between fields in every data file. */
    String SEPARATOR = ",";

    /**
     * Returns the unique id of this record (for example "V001").
     *
     * @return the record id
     */
    String getId();

    /**
     * Converts this object into one comma-separated line for its data file.
     *
     * <p><b>OOP concept - Polymorphism:</b> each class overrides this to write its own fields.</p>
     *
     * @return the line to write, without a line break
     */
    String toFileString();
}

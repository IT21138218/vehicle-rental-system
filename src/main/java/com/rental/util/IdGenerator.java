package com.rental.util;

import java.util.Collection;

/**
 * Creates the next readable id such as V001, U012 or RV003.
 *
 * <p>Rule: find the highest number already used with that prefix and add one.
 * Deleted ids are therefore never reused while a higher id still exists.</p>
 *
 * <p>This is a static utility class: it has no state, so the constructor is private.</p>
 */
public final class IdGenerator {

    private IdGenerator() {
    }

    /**
     * Returns the next id for a prefix.
     *
     * <p>Example: prefix "V" and existing ids [V001, V007, V003] gives "V008".</p>
     *
     * @param prefix      letters at the start of the id, e.g. "V"
     * @param existingIds ids already in the data file
     * @return the next free id, padded to at least three digits
     */
    public static String next(String prefix, Collection<String> existingIds) {
        int highest = 0;
        for (String id : existingIds) {
            if (id != null && id.startsWith(prefix)) {
                try {
                    int number = Integer.parseInt(id.substring(prefix.length()));
                    highest = Math.max(highest, number);
                } catch (NumberFormatException ignored) {
                    // Another prefix that starts the same way (e.g. "RV001" when prefix is "R")
                }
            }
        }
        return String.format("%s%03d", prefix, highest + 1);
    }
}

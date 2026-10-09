package com.rental.model;

import java.time.LocalDate;

/**
 * A review from a customer who has not (yet) completed a rental of this vehicle.
 *
 * <p><b>OOP concepts - Inheritance + Polymorphism:</b> extends {@link Review} and
 * overrides {@link #displayLabel()} and {@link #isVerified()}.</p>
 */
public class PublicReview extends Review {

    /** Type name stored in reviews.txt. */
    public static final String TYPE = "PUBLIC";

    /**
     * Creates a public review. Validation happens in {@link Review}.
     */
    public PublicReview(String id, String vehicleId, String customerId, int rating, LocalDate date, String comment) {
        super(id, vehicleId, customerId, rating, date, comment);
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    public String displayLabel() {
        return "Public review";
    }

    /** {@inheritDoc} */
    @Override
    public boolean isVerified() {
        return false;
    }
}

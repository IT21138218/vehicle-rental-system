package com.rental.model;

import java.time.LocalDate;

/**
 * A review from a customer who has a RETURNED rental of this vehicle.
 *
 * <p><b>OOP concepts - Inheritance + Polymorphism:</b> extends {@link Review} and
 * overrides {@link #displayLabel()} and {@link #isVerified()}.</p>
 */
public class VerifiedReview extends Review {

    /** Type name stored in reviews.txt. */
    public static final String TYPE = "VERIFIED";

    /**
     * Creates a verified review. Validation happens in {@link Review}.
     */
    public VerifiedReview(String id, String vehicleId, String customerId, int rating, LocalDate date, String comment) {
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
        return "Verified renter";
    }

    /** {@inheritDoc} */
    @Override
    public boolean isVerified() {
        return true;
    }
}

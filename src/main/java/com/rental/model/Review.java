package com.rental.model;

import com.rental.util.ValidationUtil;

import java.time.LocalDate;

/**
 * A customer's rating and comment about a vehicle.
 *
 * <p><b>OOP concepts:</b></p>
 * <ul>
 *   <li><b>Abstraction:</b> {@code Review} is abstract; a review is either a {@link PublicReview}
 *       or a {@link VerifiedReview} (the customer really rented and returned the vehicle).</li>
 *   <li><b>Encapsulation:</b> the rating must be 1-5 and the comment 3-500 characters.</li>
 *   <li><b>Polymorphism:</b> {@link #displayLabel()} and {@link #isVerified()} are answered by
 *       each subclass, so pages never need {@code instanceof}.</li>
 * </ul>
 *
 * <p>File format (reviews.txt): {@code type,id,vehicleId,customerId,rating,date,comment}.
 * The comment is the LAST field and may contain commas; the repository splits each line
 * into at most 7 parts so those commas are kept.</p>
 */
public abstract class Review implements Storable {

    /** Number of fields in a reviews.txt line. */
    public static final int FIELD_COUNT = 7;

    private final String id;
    private final String vehicleId;
    private final String customerId;
    private int rating;
    private LocalDate date;
    private String comment;

    /**
     * Creates a review; rating, date and comment are validated (Encapsulation).
     */
    protected Review(String id, String vehicleId, String customerId, int rating, LocalDate date, String comment) {
        this.id = ValidationUtil.requireText(id, "Review id", 10);
        this.vehicleId = ValidationUtil.requireText(vehicleId, "Vehicle id", 10);
        this.customerId = ValidationUtil.requireText(customerId, "Customer id", 10);
        setRating(rating);
        setDate(date);
        setComment(comment);
    }

    /**
     * Factory method: creates the correct subclass for a review type.
     *
     * @param type "PUBLIC" or "VERIFIED"
     * @return a PublicReview or a VerifiedReview
     * @throws IllegalArgumentException for an unknown type
     */
    public static Review create(String type, String id, String vehicleId, String customerId,
                                int rating, LocalDate date, String comment) {
        switch (type == null ? "" : type.trim().toUpperCase()) {
            case PublicReview.TYPE:
                return new PublicReview(id, vehicleId, customerId, rating, date, comment);
            case VerifiedReview.TYPE:
                return new VerifiedReview(id, vehicleId, customerId, rating, date, comment);
            default:
                throw new IllegalArgumentException("Review type must be PUBLIC or VERIFIED");
        }
    }

    // ----- Abstract (polymorphic) methods -----

    /**
     * @return "PUBLIC" or "VERIFIED" - the first field in reviews.txt
     */
    public abstract String getType();

    /**
     * <b>Polymorphism:</b> the badge text shown next to the review.
     *
     * @return e.g. "Verified renter"
     */
    public abstract String displayLabel();

    /**
     * <b>Polymorphism:</b> did the reviewer actually rent this vehicle?
     *
     * @return true for verified reviews
     */
    public abstract boolean isVerified();

    // ----- Storable -----

    /**
     * {@inheritDoc}
     * <p>{@link #getType()} writes the correct type for each subclass; the comment goes last.</p>
     */
    @Override
    public String toFileString() {
        return String.join(SEPARATOR, getType(), id, vehicleId, customerId,
                String.valueOf(rating), date.toString(), comment);
    }

    // ----- Getters and validating setters (Encapsulation) -----

    @Override
    public String getId() {
        return id;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public int getRating() {
        return rating;
    }

    /**
     * @param rating 1 to 5 stars
     */
    public void setRating(int rating) {
        this.rating = ValidationUtil.requireRange(rating, 1, 5, "Rating");
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Review date is required");
        }
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    /**
     * Line breaks are replaced with spaces so the review stays on one line in the file.
     * Commas are allowed because the comment is the last field.
     *
     * @param comment 3 to 500 characters
     */
    public void setComment(String comment) {
        String text = comment == null ? "" : comment.replaceAll("[\\r\\n]+", " ").trim();
        if (text.length() < 3) {
            throw new IllegalArgumentException("Comment must be at least 3 characters");
        }
        if (text.length() > 500) {
            throw new IllegalArgumentException("Comment must be at most 500 characters");
        }
        this.comment = text;
    }
}

package com.rental.repository;

import com.rental.model.Review;
import com.rental.util.ValidationUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores reviews in reviews.txt.
 *
 * <p>Line format: {@code type,id,vehicleId,customerId,rating,date,comment}<br>
 * Example: {@code VERIFIED,RV001,V001,U003,5,2026-08-06,Clean car, smooth drive}</p>
 *
 * <p>The comment is free text and may contain commas, so {@link #splitLimit()} returns 7:
 * the line is split into at most 7 parts and everything after the 6th comma is the comment.</p>
 */
public class ReviewRepository extends AbstractFileRepository<Review> {

    /** Name of the data file. */
    public static final String FILE_NAME = "reviews.txt";

    /**
     * @param filePath full path of reviews.txt
     */
    public ReviewRepository(Path filePath) {
        super(filePath);
    }

    /**
     * Keeps commas inside the last field (the comment).
     *
     * @return 7
     */
    @Override
    protected int splitLimit() {
        return Review.FIELD_COUNT;
    }

    /**
     * Factory step: field 0 ("PUBLIC" or "VERIFIED") decides which subclass is created.
     */
    @Override
    protected Review parse(String[] fields) {
        if (fields.length != Review.FIELD_COUNT) {
            throw new IllegalArgumentException("expected " + Review.FIELD_COUNT + " fields but found " + fields.length);
        }
        return Review.create(fields[0], fields[1], fields[2], fields[3],
                ValidationUtil.parseInt(fields[4], "Rating"),
                ValidationUtil.parseDate(fields[5], "Review date"),
                fields[6]);
    }

    /**
     * @return every review of a vehicle
     */
    public List<Review> findByVehicleId(String vehicleId) {
        List<Review> result = new ArrayList<>();
        for (Review review : findAll()) {
            if (review.getVehicleId().equalsIgnoreCase(vehicleId)) {
                result.add(review);
            }
        }
        return result;
    }
}

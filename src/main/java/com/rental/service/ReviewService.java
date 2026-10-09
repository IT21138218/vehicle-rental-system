package com.rental.service;

import com.rental.model.PublicReview;
import com.rental.model.Review;
import com.rental.model.User;
import com.rental.model.VerifiedReview;
import com.rental.repository.ReviewRepository;
import com.rental.repository.VehicleRepository;
import com.rental.util.IdGenerator;
import com.rental.util.ValidationUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Business rules for reviews.
 *
 * <ul>
 *   <li>Only customers write reviews, and each customer reviews a vehicle once.</li>
 *   <li>The review type is decided here: a customer with a RETURNED rental of the vehicle gets a
 *       {@link VerifiedReview}; everyone else gets a {@link PublicReview}.</li>
 *   <li>Customers edit only their own reviews; the owner or an admin may delete one.</li>
 * </ul>
 */
public class ReviewService {

    /** Prefix for review ids (RV001, RV002 ...). */
    public static final String ID_PREFIX = "RV";

    private final ReviewRepository reviewRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalService rentalService;

    /**
     * @param reviewRepository  where reviews are stored
     * @param vehicleRepository used to check the vehicle exists
     * @param rentalService     used to decide whether a review is verified
     */
    public ReviewService(ReviewRepository reviewRepository, VehicleRepository vehicleRepository,
                         RentalService rentalService) {
        this.reviewRepository = reviewRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalService = rentalService;
    }

    /**
     * Saves a new review for a vehicle.
     *
     * @return the saved review (PublicReview or VerifiedReview)
     * @throws IllegalArgumentException if a rule is broken
     */
    public Review submit(User user, String vehicleId, String ratingText, String comment) {
        if (user.canAccessAdminPages()) {
            throw new IllegalArgumentException("Reviews are written by customers, not administrators");
        }
        if (vehicleRepository.findById(vehicleId).isEmpty()) {
            throw new IllegalArgumentException("Vehicle " + vehicleId + " was not found");
        }
        Optional<Review> existing = findByCustomerAndVehicle(user.getId(), vehicleId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("You have already reviewed this vehicle (" + existing.get().getId()
                    + "). Edit your review instead.");
        }
        String id = IdGenerator.next(ID_PREFIX, allIds());
        Review review = Review.create(typeFor(user.getId(), vehicleId), id, vehicleId, user.getId(),
                ValidationUtil.parseInt(ratingText, "Rating"), LocalDate.now(), comment);
        reviewRepository.add(review);
        return review;
    }

    /**
     * Updates the customer's own review. The type is checked again, so a public review
     * becomes verified once the customer has returned a rental of that vehicle.
     *
     * @return the updated review
     */
    public Review update(String reviewId, User user, String ratingText, String comment) {
        Review old = getExisting(reviewId);
        if (!old.getCustomerId().equalsIgnoreCase(user.getId())) {
            throw new IllegalArgumentException("You can only edit your own reviews");
        }
        Review updated = Review.create(typeFor(old.getCustomerId(), old.getVehicleId()), old.getId(),
                old.getVehicleId(), old.getCustomerId(), ValidationUtil.parseInt(ratingText, "Rating"),
                LocalDate.now(), comment);
        reviewRepository.update(updated);
        return updated;
    }

    /**
     * Deletes a review; allowed for its owner or any admin.
     *
     * @return the deleted review (so the caller can redirect to its vehicle)
     */
    public Review delete(String reviewId, User user) {
        Review review = getExisting(reviewId);
        if (!canDelete(user, review)) {
            throw new IllegalArgumentException("You can only delete your own reviews");
        }
        reviewRepository.delete(review.getId());
        return review;
    }

    /**
     * @return true if the user owns the review or is an admin (polymorphic check)
     */
    public boolean canDelete(User user, Review review) {
        return user.canAccessAdminPages() || review.getCustomerId().equalsIgnoreCase(user.getId());
    }

    /**
     * @return reviews of one vehicle, newest first
     */
    public List<Review> findByVehicle(String vehicleId) {
        List<Review> result = reviewRepository.findByVehicleId(vehicleId);
        result.sort(Comparator.comparing(Review::getDate).reversed());
        return result;
    }

    /**
     * Reviews the user may manage: admins see all, customers their own. Newest first.
     */
    public List<Review> findForUser(User user) {
        List<Review> result = new ArrayList<>();
        for (Review review : reviewRepository.findAll()) {
            if (user.canAccessAdminPages() || review.getCustomerId().equalsIgnoreCase(user.getId())) {
                result.add(review);
            }
        }
        result.sort(Comparator.comparing(Review::getDate).reversed());
        return result;
    }

    /**
     * @return the review with this id, if any
     */
    public Optional<Review> findById(String reviewId) {
        return reviewRepository.findById(reviewId);
    }

    /**
     * @return the customer's review of a vehicle, if they wrote one
     */
    public Optional<Review> findByCustomerAndVehicle(String customerId, String vehicleId) {
        for (Review review : reviewRepository.findByVehicleId(vehicleId)) {
            if (review.getCustomerId().equalsIgnoreCase(customerId)) {
                return Optional.of(review);
            }
        }
        return Optional.empty();
    }

    /**
     * Average rating of every reviewed vehicle.
     *
     * @return map of vehicle id to average stars (1.0 - 5.0)
     */
    public Map<String, Double> averageRatings() {
        Map<String, int[]> sums = new HashMap<>(); // [total stars, number of reviews]
        for (Review review : reviewRepository.findAll()) {
            int[] sum = sums.computeIfAbsent(review.getVehicleId(), k -> new int[2]);
            sum[0] += review.getRating();
            sum[1]++;
        }
        Map<String, Double> averages = new HashMap<>();
        for (Map.Entry<String, int[]> entry : sums.entrySet()) {
            averages.put(entry.getKey(), Math.round(10.0 * entry.getValue()[0] / entry.getValue()[1]) / 10.0);
        }
        return averages;
    }

    // ----- helpers -----

    /** VERIFIED if the customer has returned a rental of this vehicle, otherwise PUBLIC. */
    private String typeFor(String customerId, String vehicleId) {
        return rentalService.hasReturnedRental(customerId, vehicleId) ? VerifiedReview.TYPE : PublicReview.TYPE;
    }

    private Review getExisting(String reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review " + reviewId + " was not found"));
    }

    private List<String> allIds() {
        List<String> ids = new ArrayList<>();
        for (Review review : reviewRepository.findAll()) {
            ids.add(review.getId());
        }
        return ids;
    }
}

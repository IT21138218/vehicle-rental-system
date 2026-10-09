package com.rental.servlet;

import com.rental.model.Review;
import com.rental.model.User;
import com.rental.service.RentalService;
import com.rental.service.ReviewService;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * GET /reviews/edit?id=RV001 shows the user's own review; POST saves the changes.
 * (CRUD: Update for Feedback and Reviews - owners only.)
 */
@WebServlet("/reviews/edit")
public class ReviewEditServlet extends BaseServlet {

    private static final String VIEW = "reviews/review-form";

    /** Shows the review form filled with the current rating and comment. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Optional<Review> review = findOwnReview(request);
        if (review.isEmpty()) {
            flashError(request, "You can only edit your own reviews");
            redirect(request, response, "/reviews");
            return;
        }
        showForm(request, response, review.get(), Map.of(
                "rating", String.valueOf(review.get().getRating()),
                "comment", review.get().getComment()), null);
    }

    /** Saves the new rating and comment. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Review review = getService(ReviewService.class).update(request.getParameter("id"),
                    currentUser(request), request.getParameter("rating"), request.getParameter("comment"));
            flashSuccess(request, "Review " + review.getId() + " updated (" + review.displayLabel() + ")");
            redirect(request, response, "/reviews?vehicleId=" + review.getVehicleId());
        } catch (IllegalArgumentException e) {
            Optional<Review> review = findOwnReview(request);
            if (review.isEmpty()) {
                flashError(request, e.getMessage());
                redirect(request, response, "/reviews");
                return;
            }
            showForm(request, response, review.get(), formFromRequest(request, "rating", "comment"), e.getMessage());
        }
    }

    /** The review from ?id=, but only if the logged-in user wrote it. */
    private Optional<Review> findOwnReview(HttpServletRequest request) {
        User user = currentUser(request);
        return getService(ReviewService.class).findById(request.getParameter("id"))
                .filter(review -> review.getCustomerId().equalsIgnoreCase(user.getId()));
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Review review,
                          Map<String, String> form, String error) throws ServletException, IOException {
        request.setAttribute("mode", "edit");
        request.setAttribute("review", review);
        request.setAttribute("vehicle", getService(VehicleService.class).findById(review.getVehicleId()).orElse(null));
        request.setAttribute("form", form);
        request.setAttribute("error", error);
        request.setAttribute("willBeVerified",
                getService(RentalService.class).hasReturnedRental(review.getCustomerId(), review.getVehicleId()));
        render(request, response, VIEW);
    }
}

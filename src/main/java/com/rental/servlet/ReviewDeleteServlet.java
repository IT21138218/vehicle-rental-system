package com.rental.servlet;

import com.rental.model.Review;
import com.rental.service.ReviewService;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * GET /reviews/delete?id=RV001 asks for confirmation; POST deletes the review.
 * Allowed for the review's owner or any admin.
 * (CRUD: Delete for Feedback and Reviews.)
 */
@WebServlet("/reviews/delete")
public class ReviewDeleteServlet extends BaseServlet {

    /** Shows the "Delete this review?" page. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ReviewService reviewService = getService(ReviewService.class);
        Optional<Review> review = reviewService.findById(request.getParameter("id"))
                .filter(r -> reviewService.canDelete(currentUser(request), r));
        if (review.isEmpty()) {
            flashError(request, "You can only delete your own reviews");
            redirect(request, response, "/reviews");
            return;
        }
        request.setAttribute("review", review.get());
        request.setAttribute("vehicle", getService(VehicleService.class).findById(review.get().getVehicleId()).orElse(null));
        render(request, response, "reviews/review-delete");
    }

    /** Deletes the review and goes back to the vehicle's reviews. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Review review = getService(ReviewService.class).delete(request.getParameter("id"), currentUser(request));
            flashSuccess(request, "Review " + review.getId() + " deleted");
            redirect(request, response, "/reviews?vehicleId=" + review.getVehicleId());
        } catch (IllegalArgumentException e) {
            flashError(request, e.getMessage());
            redirect(request, response, "/reviews");
        }
    }
}

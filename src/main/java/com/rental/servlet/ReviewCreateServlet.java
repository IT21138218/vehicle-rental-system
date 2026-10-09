package com.rental.servlet;

import com.rental.model.Review;
import com.rental.model.User;
import com.rental.model.Vehicle;
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
 * GET /reviews/new?vehicleId=V001 shows the review form; POST saves the review.
 * (CRUD: Create for Feedback and Reviews.)
 */
@WebServlet("/reviews/new")
public class ReviewCreateServlet extends BaseServlet {

    private static final String VIEW = "reviews/review-form";

    /** Shows the empty review form (or the edit form if the user already reviewed it). */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        Optional<Vehicle> vehicle = getService(VehicleService.class).findById(request.getParameter("vehicleId"));
        if (vehicle.isEmpty()) {
            flashError(request, "Vehicle not found");
            redirect(request, response, "/vehicles");
            return;
        }
        if (user.canAccessAdminPages()) {
            flashError(request, "Reviews are written by customers, not administrators");
            redirect(request, response, "/reviews?vehicleId=" + vehicle.get().getId());
            return;
        }
        Optional<Review> existing = getService(ReviewService.class)
                .findByCustomerAndVehicle(user.getId(), vehicle.get().getId());
        if (existing.isPresent()) {
            redirect(request, response, "/reviews/edit?id=" + existing.get().getId());
            return;
        }
        showForm(request, response, vehicle.get(), Map.of("rating", "5", "comment", ""), null);
    }

    /** Saves the review, then shows the vehicle's reviews (Post-Redirect-Get). */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String vehicleId = request.getParameter("vehicleId");
        try {
            Review review = getService(ReviewService.class).submit(currentUser(request), vehicleId,
                    request.getParameter("rating"), request.getParameter("comment"));
            flashSuccess(request, "Thank you! Your review " + review.getId() + " was posted as: " + review.displayLabel());
            redirect(request, response, "/reviews?vehicleId=" + review.getVehicleId());
        } catch (IllegalArgumentException e) {
            Optional<Vehicle> vehicle = getService(VehicleService.class).findById(vehicleId);
            if (vehicle.isEmpty()) {
                flashError(request, e.getMessage());
                redirect(request, response, "/vehicles");
                return;
            }
            showForm(request, response, vehicle.get(), formFromRequest(request, "rating", "comment"), e.getMessage());
        }
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Vehicle vehicle,
                          Map<String, String> form, String error) throws ServletException, IOException {
        request.setAttribute("mode", "new");
        request.setAttribute("vehicle", vehicle);
        request.setAttribute("form", form);
        request.setAttribute("error", error);
        request.setAttribute("willBeVerified",
                getService(RentalService.class).hasReturnedRental(currentUser(request).getId(), vehicle.getId()));
        render(request, response, VIEW);
    }
}

package com.rental.servlet;

import com.rental.model.User;
import com.rental.model.Vehicle;
import com.rental.service.ReviewService;
import com.rental.service.UserService;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * GET /reviews?vehicleId=V001 shows all reviews of one vehicle.
 * GET /reviews (no vehicle) shows the user's own reviews, or every review for an admin.
 * (CRUD: Read for Feedback and Reviews.)
 */
@WebServlet("/reviews")
public class ReviewListServlet extends BaseServlet {

    /** Shows reviews for a vehicle or for the user. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        ReviewService reviewService = getService(ReviewService.class);
        String vehicleId = request.getParameter("vehicleId");

        if (vehicleId != null && !vehicleId.isBlank()) {
            Optional<Vehicle> vehicle = getService(VehicleService.class).findById(vehicleId);
            if (vehicle.isEmpty()) {
                flashError(request, "Vehicle not found");
                redirect(request, response, "/vehicles");
                return;
            }
            request.setAttribute("vehicle", vehicle.get());
            request.setAttribute("reviews", reviewService.findByVehicle(vehicle.get().getId()));
            request.setAttribute("average", reviewService.averageRatings().get(vehicle.get().getId()));
            request.setAttribute("myReview",
                    reviewService.findByCustomerAndVehicle(user.getId(), vehicle.get().getId()).orElse(null));
        } else {
            request.setAttribute("reviews", reviewService.findForUser(user));
        }
        request.setAttribute("usersById", getService(UserService.class).findAllAsMap());
        request.setAttribute("vehiclesById", getService(VehicleService.class).findAllAsMap());
        render(request, response, "reviews/review-list");
    }
}

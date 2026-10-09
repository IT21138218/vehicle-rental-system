package com.rental.servlet;

import com.rental.model.Rental;
import com.rental.model.User;
import com.rental.service.RentalService;
import com.rental.service.UserService;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * GET /rentals lists bookings: admins see every booking, customers only their own
 * (decided polymorphically inside RentalService). Optional filter ?status=ACTIVE.
 * (CRUD: Read for Rental Booking.)
 */
@WebServlet("/rentals")
public class RentalListServlet extends BaseServlet {

    /** Shows the rentals table with names and estimated costs. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        RentalService rentalService = getService(RentalService.class);
        List<Rental> rentals = rentalService.findForUser(user, request.getParameter("status"));

        Map<String, Double> costs = new HashMap<>();
        for (Rental rental : rentals) {
            costs.put(rental.getId(), rentalService.estimateCost(rental));
        }
        request.setAttribute("rentals", rentals);
        request.setAttribute("costs", costs);
        request.setAttribute("vehiclesById", getService(VehicleService.class).findAllAsMap());
        request.setAttribute("usersById", getService(UserService.class).findAllAsMap());
        render(request, response, "rentals/rental-list");
    }
}

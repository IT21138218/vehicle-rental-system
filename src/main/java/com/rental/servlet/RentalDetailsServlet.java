package com.rental.servlet;

import com.rental.model.Rental;
import com.rental.service.RentalService;
import com.rental.service.UserService;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * GET /rentals/view?id=R008 shows one booking. Customers can only open their own.
 */
@WebServlet("/rentals/view")
public class RentalDetailsServlet extends BaseServlet {

    /** Shows the booking details and the actions allowed for its status. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RentalService rentalService = getService(RentalService.class);
        Optional<Rental> rental = rentalService.findVisible(request.getParameter("id"), currentUser(request));
        if (rental.isEmpty()) {
            flashError(request, "Booking not found");
            redirect(request, response, "/rentals");
            return;
        }
        request.setAttribute("rental", rental.get());
        request.setAttribute("vehicle", getService(VehicleService.class).findById(rental.get().getVehicleId()).orElse(null));
        request.setAttribute("customer", getService(UserService.class).findById(rental.get().getCustomerId()).orElse(null));
        request.setAttribute("cost", rentalService.estimateCost(rental.get()));
        render(request, response, "rentals/rental-details");
    }
}

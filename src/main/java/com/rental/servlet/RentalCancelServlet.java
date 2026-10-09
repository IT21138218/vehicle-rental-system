package com.rental.servlet;

import com.rental.model.Rental;
import com.rental.service.RentalService;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * GET /rentals/cancel?id=R008 asks for confirmation; POST cancels the booking.
 * (CRUD: Delete for Rental Booking - the record is kept with status CANCELLED so the
 * history is not lost.)
 */
@WebServlet("/rentals/cancel")
public class RentalCancelServlet extends BaseServlet {

    /** Shows the "Cancel this booking?" page. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RentalService rentalService = getService(RentalService.class);
        Optional<Rental> rental = rentalService.findVisible(request.getParameter("id"), currentUser(request));
        if (rental.isEmpty() || !rental.get().isActive()) {
            flashError(request, "Only your active bookings can be cancelled");
            redirect(request, response, "/rentals");
            return;
        }
        request.setAttribute("rental", rental.get());
        request.setAttribute("vehicle", getService(VehicleService.class).findById(rental.get().getVehicleId()).orElse(null));
        request.setAttribute("cost", rentalService.estimateCost(rental.get()));
        render(request, response, "rentals/rental-cancel");
    }

    /** Cancels the booking and returns to the list. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        try {
            getService(RentalService.class).cancel(id, currentUser(request));
            flashSuccess(request, "Booking " + id + " cancelled");
        } catch (IllegalArgumentException e) {
            flashError(request, e.getMessage());
        }
        redirect(request, response, "/rentals");
    }
}

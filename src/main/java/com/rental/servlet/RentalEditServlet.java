package com.rental.servlet;

import com.rental.model.Rental;
import com.rental.service.RentalService;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * GET /rentals/edit?id=R008 shows the date form; POST saves the new dates.
 * (CRUD: Update for Rental Booking. Only ACTIVE bookings can be changed.)
 */
@WebServlet("/rentals/edit")
public class RentalEditServlet extends BaseServlet {

    private static final String VIEW = "rentals/rental-form";

    /** Shows the booking's current dates. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Optional<Rental> rental = getService(RentalService.class)
                .findVisible(request.getParameter("id"), currentUser(request));
        if (rental.isEmpty()) {
            flashError(request, "Booking not found");
            redirect(request, response, "/rentals");
            return;
        }
        if (!rental.get().isActive()) {
            flashError(request, "Only active bookings can be changed");
            redirect(request, response, "/rentals/view?id=" + rental.get().getId());
            return;
        }
        showForm(request, response, rental.get(), Map.of(
                "startDate", rental.get().getStartDate().toString(),
                "endDate", rental.get().getEndDate().toString()), null);
    }

    /** Saves the new dates if they pass the booking rules. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        RentalService rentalService = getService(RentalService.class);
        try {
            Rental rental = rentalService.modifyDates(id, currentUser(request),
                    request.getParameter("startDate"), request.getParameter("endDate"));
            flashSuccess(request, "Booking " + rental.getId() + " now runs from "
                    + rental.getStartDate() + " to " + rental.getEndDate());
            redirect(request, response, "/rentals/view?id=" + rental.getId());
        } catch (IllegalArgumentException e) {
            Optional<Rental> rental = rentalService.findVisible(id, currentUser(request));
            if (rental.isEmpty() || !rental.get().isActive()) {
                flashError(request, e.getMessage());
                redirect(request, response, "/rentals");
                return;
            }
            showForm(request, response, rental.get(), formFromRequest(request, "startDate", "endDate"), e.getMessage());
        }
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Rental rental,
                          Map<String, String> form, String error) throws ServletException, IOException {
        request.setAttribute("mode", "edit");
        request.setAttribute("rental", rental);
        request.setAttribute("vehicle", getService(VehicleService.class).findById(rental.getVehicleId()).orElse(null));
        request.setAttribute("form", form);
        request.setAttribute("error", error);
        request.setAttribute("today", LocalDate.now().toString());
        request.setAttribute("upcoming", getService(RentalService.class).findUpcomingForVehicle(rental.getVehicleId()));
        render(request, response, VIEW);
    }
}

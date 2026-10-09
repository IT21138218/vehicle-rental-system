package com.rental.servlet;

import com.rental.model.Rental;
import com.rental.model.Vehicle;
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
 * GET /rentals/new?vehicleId=V001 shows the booking form; POST creates the booking.
 * (CRUD: Create for Rental Booking.)
 */
@WebServlet("/rentals/new")
public class RentalCreateServlet extends BaseServlet {

    private static final String VIEW = "rentals/rental-form";

    /** Shows the booking form with suggested dates and already-booked periods. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Optional<Vehicle> vehicle = getService(VehicleService.class).findById(request.getParameter("vehicleId"));
        if (vehicle.isEmpty() || !vehicle.get().isAvailable()) {
            flashError(request, "That vehicle is not available for booking");
            redirect(request, response, "/vehicles");
            return;
        }
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        showForm(request, response, vehicle.get(),
                Map.of("startDate", tomorrow.toString(), "endDate", tomorrow.plusDays(3).toString()), null);
    }

    /** Books the vehicle, then shows the new booking (Post-Redirect-Get). */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String vehicleId = request.getParameter("vehicleId");
        RentalService rentalService = getService(RentalService.class);
        try {
            Rental rental = rentalService.book(currentUser(request), vehicleId,
                    request.getParameter("startDate"), request.getParameter("endDate"));
            flashSuccess(request, String.format("Booking %s confirmed for %d day(s). Estimated cost: Rs. %,.2f",
                    rental.getId(), rental.getDays(), rentalService.estimateCost(rental)));
            redirect(request, response, "/rentals/view?id=" + rental.getId());
        } catch (IllegalArgumentException e) {
            Optional<Vehicle> vehicle = getService(VehicleService.class).findById(vehicleId);
            if (vehicle.isEmpty()) {
                flashError(request, e.getMessage());
                redirect(request, response, "/vehicles");
                return;
            }
            showForm(request, response, vehicle.get(), formFromRequest(request, "startDate", "endDate"), e.getMessage());
        }
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Vehicle vehicle,
                          Map<String, String> form, String error) throws ServletException, IOException {
        request.setAttribute("mode", "new");
        request.setAttribute("vehicle", vehicle);
        request.setAttribute("form", form);
        request.setAttribute("error", error);
        request.setAttribute("today", LocalDate.now().toString());
        request.setAttribute("upcoming", getService(RentalService.class).findUpcomingForVehicle(vehicle.getId()));
        render(request, response, VIEW);
    }
}

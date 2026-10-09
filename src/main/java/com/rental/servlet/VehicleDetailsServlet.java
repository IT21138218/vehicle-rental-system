package com.rental.servlet;

import com.rental.model.Vehicle;
import com.rental.service.RentalService;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * GET /vehicles/view?id=V001 shows one vehicle with its price breakdown and booked dates.
 */
@WebServlet("/vehicles/view")
public class VehicleDetailsServlet extends BaseServlet {

    /** Shows the vehicle details page. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Optional<Vehicle> vehicle = getService(VehicleService.class).findById(request.getParameter("id"));
        if (vehicle.isEmpty()) {
            flashError(request, "Vehicle not found");
            redirect(request, response, "/vehicles");
            return;
        }
        request.setAttribute("vehicle", vehicle.get());
        request.setAttribute("upcoming", getService(RentalService.class).findUpcomingForVehicle(vehicle.get().getId()));
        render(request, response, "vehicles/vehicle-details");
    }
}

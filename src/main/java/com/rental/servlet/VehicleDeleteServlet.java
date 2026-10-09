package com.rental.servlet;

import com.rental.model.Vehicle;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * GET /admin/vehicles/delete?id=V001 asks for confirmation; POST deletes the vehicle.
 * (CRUD: Delete for Vehicle Management.)
 */
@WebServlet("/admin/vehicles/delete")
public class VehicleDeleteServlet extends BaseServlet {

    /** Shows the "Are you sure?" page. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (refuseUnless(currentUser(request).canModifyVehicles(), response)) {
            return;
        }
        Optional<Vehicle> vehicle = getService(VehicleService.class).findById(request.getParameter("id"));
        if (vehicle.isEmpty()) {
            flashError(request, "Vehicle not found");
            redirect(request, response, "/admin/vehicles");
            return;
        }
        request.setAttribute("vehicle", vehicle.get());
        render(request, response, "vehicles/vehicle-delete");
    }

    /** Deletes the vehicle (if the business rules allow it) and returns to the list. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (refuseUnless(currentUser(request).canModifyVehicles(), response)) {
            return;
        }
        String id = request.getParameter("id");
        try {
            getService(VehicleService.class).delete(id);
            flashSuccess(request, "Vehicle " + id + " deleted");
        } catch (IllegalArgumentException e) {
            flashError(request, e.getMessage());
        }
        redirect(request, response, "/admin/vehicles");
    }
}

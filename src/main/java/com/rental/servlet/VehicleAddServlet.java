package com.rental.servlet;

import com.rental.model.Vehicle;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * GET /admin/vehicles/add shows an empty vehicle form; POST saves the new vehicle.
 * (CRUD: Create for Vehicle Management.)
 *
 * <p><b>OOP concept - Polymorphism:</b> access is checked with
 * {@code currentUser.canModifyVehicles()}, answered differently by AdminUser and Customer.</p>
 */
@WebServlet("/admin/vehicles/add")
public class VehicleAddServlet extends BaseServlet {

    private static final String VIEW = "vehicles/vehicle-form";

    /** Shows the empty form (new vehicles are available by default). */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (refuseUnless(currentUser(request).canModifyVehicles(), response)) {
            return;
        }
        request.setAttribute("mode", "add");
        request.setAttribute("form", Map.of("type", "CAR", "available", "on"));
        render(request, response, VIEW);
    }

    /** Validates and saves the vehicle, then redirects to the list (Post-Redirect-Get). */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (refuseUnless(currentUser(request).canModifyVehicles(), response)) {
            return;
        }
        try {
            Vehicle vehicle = getService(VehicleService.class).add(
                    request.getParameter("type"),
                    request.getParameter("brand"),
                    request.getParameter("model"),
                    request.getParameter("year"),
                    request.getParameter("baseDailyRate"),
                    request.getParameter("available") != null,
                    request.getParameter("spec"));
            flashSuccess(request, "Vehicle " + vehicle.getId() + " (" + vehicle.displayDetails() + ") added");
            redirect(request, response, "/admin/vehicles");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("mode", "add");
            request.setAttribute("form", formFromRequest(request,
                    "type", "brand", "model", "year", "baseDailyRate", "available", "spec"));
            render(request, response, VIEW);
        }
    }
}

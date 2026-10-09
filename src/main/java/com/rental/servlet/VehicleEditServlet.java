package com.rental.servlet;

import com.rental.model.Vehicle;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * GET /admin/vehicles/edit?id=V001 shows the filled form; POST saves the changes.
 * (CRUD: Update for Vehicle Management.)
 */
@WebServlet("/admin/vehicles/edit")
public class VehicleEditServlet extends BaseServlet {

    private static final String VIEW = "vehicles/vehicle-form";

    /** Shows the form with the vehicle's current values. */
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
        Vehicle v = vehicle.get();
        Map<String, String> form = new HashMap<>();
        form.put("type", v.getType());
        form.put("brand", v.getBrand());
        form.put("model", v.getModel());
        form.put("year", String.valueOf(v.getYear()));
        form.put("baseDailyRate", String.valueOf(v.getBaseDailyRate()));
        form.put("available", v.isAvailable() ? "on" : "");
        form.put("spec", String.valueOf(v.getSpecValue())); // polymorphic: seats, cc or kg
        showForm(request, response, v, form, null);
    }

    /** Saves the changes, then redirects to the list. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (refuseUnless(currentUser(request).canModifyVehicles(), response)) {
            return;
        }
        VehicleService vehicleService = getService(VehicleService.class);
        String id = request.getParameter("id");
        try {
            Vehicle vehicle = vehicleService.update(id,
                    request.getParameter("brand"),
                    request.getParameter("model"),
                    request.getParameter("year"),
                    request.getParameter("baseDailyRate"),
                    request.getParameter("available") != null,
                    request.getParameter("spec"));
            flashSuccess(request, "Vehicle " + vehicle.getId() + " updated");
            redirect(request, response, "/admin/vehicles");
        } catch (IllegalArgumentException e) {
            Optional<Vehicle> vehicle = vehicleService.findById(id);
            if (vehicle.isEmpty()) {
                flashError(request, e.getMessage());
                redirect(request, response, "/admin/vehicles");
                return;
            }
            Map<String, String> form = formFromRequest(request,
                    "brand", "model", "year", "baseDailyRate", "available", "spec");
            form.put("type", vehicle.get().getType());
            showForm(request, response, vehicle.get(), form, e.getMessage());
        }
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Vehicle vehicle,
                          Map<String, String> form, String error) throws ServletException, IOException {
        request.setAttribute("mode", "edit");
        request.setAttribute("vehicle", vehicle);
        request.setAttribute("form", form);
        request.setAttribute("error", error);
        render(request, response, VIEW);
    }
}

package com.rental.servlet;

import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /admin/vehicles lists the fleet with search (?q=), type (?type=) and
 * availability (?availability=available|unavailable) filters.
 * (CRUD: Read/Search for Vehicle Management.)
 */
@WebServlet("/admin/vehicles")
public class VehicleListServlet extends BaseServlet {

    /** Shows the filtered vehicle table. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        VehicleService vehicleService = getService(VehicleService.class);
        request.setAttribute("vehicles", vehicleService.search(request.getParameter("q"),
                request.getParameter("type"), request.getParameter("availability")));
        request.setAttribute("typeCounts", vehicleService.countByType());
        render(request, response, "vehicles/vehicle-list");
    }
}

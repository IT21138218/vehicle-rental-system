package com.rental.servlet;

import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /vehicles lets customers browse and search vehicles that are in service.
 * (CRUD: Read/Search, customer side.)
 */
@WebServlet("/vehicles")
public class VehicleBrowseServlet extends BaseServlet {

    /** Shows available vehicles as cards, filtered by keyword and type. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("vehicles", getService(VehicleService.class).search(
                request.getParameter("q"), request.getParameter("type"), VehicleService.AVAILABLE));
        render(request, response, "vehicles/vehicle-browse");
    }
}

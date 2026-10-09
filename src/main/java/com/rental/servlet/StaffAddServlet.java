package com.rental.servlet;

import com.rental.model.Staff;
import com.rental.service.StaffService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * GET /admin/staff/add shows an empty staff form; POST saves the new driver or mechanic.
 * (CRUD: Create for Driver/Staff Management.)
 */
@WebServlet("/admin/staff/add")
public class StaffAddServlet extends BaseServlet {

    private static final String VIEW = "staff/staff-form";

    /** Shows the empty form. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("mode", "add");
        request.setAttribute("form", Map.of("type", "DRIVER"));
        render(request, response, VIEW);
    }

    /** Validates and saves, then redirects to the list. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Staff staff = getService(StaffService.class).add(
                    request.getParameter("type"),
                    request.getParameter("name"),
                    request.getParameter("phone"),
                    request.getParameter("dailyWage"),
                    request.getParameter("extra"));
            flashSuccess(request, staff.getId() + " " + staff.getName() + " added as " + staff.displayRole());
            redirect(request, response, "/admin/staff");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("mode", "add");
            request.setAttribute("form", formFromRequest(request, "type", "name", "phone", "dailyWage", "extra"));
            render(request, response, VIEW);
        }
    }
}

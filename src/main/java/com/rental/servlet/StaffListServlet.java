package com.rental.servlet;

import com.rental.service.StaffService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /admin/staff lists drivers and mechanics with search (?q=) and type (?type=) filters.
 * (CRUD: Read for Driver/Staff Management.)
 */
@WebServlet("/admin/staff")
public class StaffListServlet extends BaseServlet {

    /** Number of working days used for the "monthly pay" column. */
    public static final int STANDARD_WORKING_DAYS = 22;

    /** Shows the staff table. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StaffService staffService = getService(StaffService.class);
        request.setAttribute("staffList", staffService.search(request.getParameter("q"), request.getParameter("type")));
        request.setAttribute("typeCounts", staffService.countByType());
        request.setAttribute("workingDays", STANDARD_WORKING_DAYS);
        render(request, response, "staff/staff-list");
    }
}

package com.rental.servlet;

import com.rental.model.Staff;
import com.rental.service.StaffService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * GET /admin/staff/delete?id=S001 asks for confirmation; POST removes the staff member.
 * (CRUD: Delete for Driver/Staff Management.)
 */
@WebServlet("/admin/staff/delete")
public class StaffDeleteServlet extends BaseServlet {

    /** Shows the "Remove this staff member?" page. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Optional<Staff> staff = getService(StaffService.class).findById(request.getParameter("id"));
        if (staff.isEmpty()) {
            flashError(request, "Staff member not found");
            redirect(request, response, "/admin/staff");
            return;
        }
        request.setAttribute("staff", staff.get());
        render(request, response, "staff/staff-delete");
    }

    /** Removes the staff member and returns to the list. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        try {
            getService(StaffService.class).delete(id);
            flashSuccess(request, "Staff member " + id + " removed");
        } catch (IllegalArgumentException e) {
            flashError(request, e.getMessage());
        }
        redirect(request, response, "/admin/staff");
    }
}

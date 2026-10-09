package com.rental.servlet;

import com.rental.model.Staff;
import com.rental.service.StaffService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * GET /admin/staff/edit?id=S001 shows the filled form; POST saves the changes.
 * (CRUD: Update for Driver/Staff Management.)
 */
@WebServlet("/admin/staff/edit")
public class StaffEditServlet extends BaseServlet {

    private static final String VIEW = "staff/staff-form";

    /** Shows the form with the current values. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Optional<Staff> staff = getService(StaffService.class).findById(request.getParameter("id"));
        if (staff.isEmpty()) {
            flashError(request, "Staff member not found");
            redirect(request, response, "/admin/staff");
            return;
        }
        Map<String, String> form = new HashMap<>();
        form.put("type", staff.get().getType());
        form.put("name", staff.get().getName());
        form.put("phone", staff.get().getPhone());
        form.put("dailyWage", String.valueOf(staff.get().getDailyWage()));
        form.put("extra", staff.get().getExtraValue()); // polymorphic: licence or specialization
        showForm(request, response, staff.get(), form, null);
    }

    /** Saves the changes, then redirects to the list. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        StaffService staffService = getService(StaffService.class);
        String id = request.getParameter("id");
        try {
            Staff staff = staffService.update(id, request.getParameter("name"), request.getParameter("phone"),
                    request.getParameter("dailyWage"), request.getParameter("extra"));
            flashSuccess(request, "Staff member " + staff.getId() + " updated");
            redirect(request, response, "/admin/staff");
        } catch (IllegalArgumentException e) {
            Optional<Staff> staff = staffService.findById(id);
            if (staff.isEmpty()) {
                flashError(request, e.getMessage());
                redirect(request, response, "/admin/staff");
                return;
            }
            Map<String, String> form = formFromRequest(request, "name", "phone", "dailyWage", "extra");
            form.put("type", staff.get().getType());
            showForm(request, response, staff.get(), form, e.getMessage());
        }
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, Staff staff,
                          Map<String, String> form, String error) throws ServletException, IOException {
        request.setAttribute("mode", "edit");
        request.setAttribute("staff", staff);
        request.setAttribute("form", form);
        request.setAttribute("error", error);
        render(request, response, VIEW);
    }
}

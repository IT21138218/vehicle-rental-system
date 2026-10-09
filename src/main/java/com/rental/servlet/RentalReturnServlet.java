package com.rental.servlet;

import com.rental.service.RentalService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * POST /admin/rentals/return marks an ACTIVE booking as RETURNED (admin only - the
 * AuthFilter protects every /admin/ URL).
 */
@WebServlet("/admin/rentals/return")
public class RentalReturnServlet extends BaseServlet {

    /** Marks the rental returned and goes back to its details page. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        try {
            getService(RentalService.class).markReturned(id);
            flashSuccess(request, "Rental " + id + " marked as returned");
        } catch (IllegalArgumentException e) {
            flashError(request, e.getMessage());
        }
        redirect(request, response, "/rentals/view?id=" + id);
    }
}

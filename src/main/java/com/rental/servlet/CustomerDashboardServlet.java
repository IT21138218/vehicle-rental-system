package com.rental.servlet;

import com.rental.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /customer/dashboard shows the customer's home page.
 *
 * <p><b>OOP concept - Polymorphism:</b> if an admin opens this URL they are redirected to
 * their own {@code getDashboardPath()} - no {@code instanceof} needed.</p>
 */
@WebServlet("/customer/dashboard")
public class CustomerDashboardServlet extends BaseServlet {

    /** Shows the customer dashboard. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        if (!request.getServletPath().equals(user.getDashboardPath())) {
            redirect(request, response, user.getDashboardPath());
            return;
        }
        render(request, response, "dashboard/customer-dashboard");
    }
}

package com.rental.servlet;

import com.rental.model.AdminUser;
import com.rental.model.Customer;
import com.rental.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /admin/dashboard shows summary numbers for the administrator.
 * (Protected by the AuthFilter: only users whose canAccessAdminPages() is true get here.)
 */
@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {

    /** Collects the totals and shows the admin dashboard. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserService userService = getService(UserService.class);
        request.setAttribute("customerCount", userService.countByRole(Customer.ROLE));
        request.setAttribute("adminCount", userService.countByRole(AdminUser.ROLE));
        render(request, response, "dashboard/admin-dashboard");
    }
}

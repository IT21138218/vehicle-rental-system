package com.rental.servlet;

import com.rental.model.AdminUser;
import com.rental.model.Customer;
import com.rental.model.RentalStatus;
import com.rental.service.RentalService;
import com.rental.service.UserService;
import com.rental.service.VehicleService;
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

        VehicleService vehicleService = getService(VehicleService.class);
        request.setAttribute("vehicleCount", vehicleService.findAll().size());
        request.setAttribute("availableVehicleCount", vehicleService.countAvailable());
        request.setAttribute("vehicleTypeCounts", vehicleService.countByType());

        RentalService rentalService = getService(RentalService.class);
        request.setAttribute("activeRentalCount", rentalService.countByStatus(RentalStatus.ACTIVE));
        request.setAttribute("returnedRentalCount", rentalService.countByStatus(RentalStatus.RETURNED));
        render(request, response, "dashboard/admin-dashboard");
    }
}

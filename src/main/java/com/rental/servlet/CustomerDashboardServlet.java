package com.rental.servlet;

import com.rental.model.RentalStatus;
import com.rental.model.User;
import com.rental.service.RentalService;
import com.rental.service.VehicleService;
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
        RentalService rentalService = getService(RentalService.class);
        request.setAttribute("activeRentalCount", rentalService.countForCustomer(user.getId(), RentalStatus.ACTIVE));
        request.setAttribute("returnedRentalCount", rentalService.countForCustomer(user.getId(), RentalStatus.RETURNED));
        request.setAttribute("activeRentals", rentalService.findForUser(user, RentalStatus.ACTIVE.name()));
        request.setAttribute("vehiclesById", getService(VehicleService.class).findAllAsMap());
        render(request, response, "dashboard/customer-dashboard");
    }
}

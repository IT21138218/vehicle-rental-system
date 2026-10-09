package com.rental.servlet;

import com.rental.model.Payment;
import com.rental.model.Rental;
import com.rental.service.PaymentService;
import com.rental.service.RentalService;
import com.rental.service.UserService;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * GET /payments/view?id=P001 shows one printable bill. Customers can only open their own.
 *
 * <p><b>OOP concept - Polymorphism:</b> the page calls getLateFee(), getSurcharge() and
 * calculateTotal() on a Payment reference; cash and card bills give different results.</p>
 */
@WebServlet("/payments/view")
public class PaymentViewServlet extends BaseServlet {

    /** Shows the bill. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Optional<Payment> payment = getService(PaymentService.class)
                .findVisible(request.getParameter("id"), currentUser(request));
        if (payment.isEmpty()) {
            flashError(request, "Bill not found");
            redirect(request, response, "/payments");
            return;
        }
        Optional<Rental> rental = getService(RentalService.class).findById(payment.get().getRentalId());
        request.setAttribute("payment", payment.get());
        request.setAttribute("rental", rental.orElse(null));
        request.setAttribute("vehicle", rental.flatMap(r -> getService(VehicleService.class).findById(r.getVehicleId())).orElse(null));
        request.setAttribute("customer", getService(UserService.class).findById(payment.get().getCustomerId()).orElse(null));
        render(request, response, "payments/bill");
    }
}

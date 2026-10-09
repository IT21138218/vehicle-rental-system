package com.rental.servlet;

import com.rental.model.Payment;
import com.rental.service.PaymentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * GET /admin/payments/edit?id=P001 shows the status form; POST saves it.
 * (CRUD: Update for Payment and Billing - e.g. mark a bill PAID or add late days.)
 */
@WebServlet("/admin/payments/edit")
public class PaymentEditServlet extends BaseServlet {

    private static final String VIEW = "payments/payment-edit";

    /** Shows the bill's current status, late days and method detail. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Optional<Payment> payment = getService(PaymentService.class).findById(request.getParameter("id"));
        if (payment.isEmpty()) {
            flashError(request, "Bill not found");
            redirect(request, response, "/payments");
            return;
        }
        request.setAttribute("payment", payment.get());
        request.setAttribute("form", Map.of(
                "status", payment.get().getStatus().name(),
                "lateDays", String.valueOf(payment.get().getLateDays()),
                "extra", payment.get().getExtraValue()));
        render(request, response, VIEW);
    }

    /** Saves the changes and opens the bill. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PaymentService paymentService = getService(PaymentService.class);
        String id = request.getParameter("id");
        try {
            Payment payment = paymentService.update(id, request.getParameter("status"),
                    request.getParameter("lateDays"), request.getParameter("extra"));
            flashSuccess(request, String.format("Bill %s updated (%s). Total: Rs. %,.2f",
                    payment.getId(), payment.getStatus(), payment.calculateTotal()));
            redirect(request, response, "/payments/view?id=" + payment.getId());
        } catch (IllegalArgumentException e) {
            Optional<Payment> payment = paymentService.findById(id);
            if (payment.isEmpty()) {
                flashError(request, e.getMessage());
                redirect(request, response, "/payments");
                return;
            }
            request.setAttribute("error", e.getMessage());
            request.setAttribute("payment", payment.get());
            request.setAttribute("form", formFromRequest(request, "status", "lateDays", "extra"));
            render(request, response, VIEW);
        }
    }
}

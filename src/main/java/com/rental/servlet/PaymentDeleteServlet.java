package com.rental.servlet;

import com.rental.model.Payment;
import com.rental.service.PaymentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * GET /admin/payments/delete?id=P001 asks for confirmation; POST deletes the bill.
 * (CRUD: Delete for Payment and Billing.)
 */
@WebServlet("/admin/payments/delete")
public class PaymentDeleteServlet extends BaseServlet {

    /** Shows the "Delete this bill?" page. */
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
        render(request, response, "payments/payment-delete");
    }

    /** Deletes the bill and returns to the list. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        try {
            getService(PaymentService.class).delete(id);
            flashSuccess(request, "Bill " + id + " deleted");
        } catch (IllegalArgumentException e) {
            flashError(request, e.getMessage());
        }
        redirect(request, response, "/payments");
    }
}

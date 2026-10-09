package com.rental.servlet;

import com.rental.model.Payment;
import com.rental.service.PaymentService;
import com.rental.service.UserService;
import com.rental.service.VehicleService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * GET /admin/payments/generate[?rentalId=R001] shows the "generate bill" form;
 * POST creates the bill. (CRUD: Create for Payment and Billing - admin only.)
 */
@WebServlet("/admin/payments/generate")
public class PaymentGenerateServlet extends BaseServlet {

    private static final String VIEW = "payments/payment-form";

    /** Shows the form; the rental can be pre-selected with ?rentalId=. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<String, String> form = new HashMap<>();
        form.put("rentalId", request.getParameter("rentalId") == null ? "" : request.getParameter("rentalId"));
        form.put("method", "CASH");
        form.put("lateDays", "0");
        form.put("extra", "Front Desk");
        showForm(request, response, form, null);
    }

    /** Generates the bill and opens it (Post-Redirect-Get). */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Payment payment = getService(PaymentService.class).generate(
                    request.getParameter("rentalId"),
                    request.getParameter("method"),
                    request.getParameter("lateDays"),
                    request.getParameter("extra"));
            flashSuccess(request, String.format("Bill %s created. Total: Rs. %,.2f",
                    payment.getId(), payment.calculateTotal()));
            redirect(request, response, "/payments/view?id=" + payment.getId());
        } catch (IllegalArgumentException e) {
            showForm(request, response, formFromRequest(request, "rentalId", "method", "lateDays", "extra"), e.getMessage());
        }
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response,
                          Map<String, String> form, String error) throws ServletException, IOException {
        request.setAttribute("form", form);
        request.setAttribute("error", error);
        request.setAttribute("billableRentals", getService(PaymentService.class).findBillableRentals());
        request.setAttribute("vehiclesById", getService(VehicleService.class).findAllAsMap());
        request.setAttribute("usersById", getService(UserService.class).findAllAsMap());
        render(request, response, VIEW);
    }
}

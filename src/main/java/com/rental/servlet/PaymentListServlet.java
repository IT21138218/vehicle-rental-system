package com.rental.servlet;

import com.rental.model.PaymentStatus;
import com.rental.model.User;
import com.rental.service.PaymentService;
import com.rental.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /payments lists bills: admins see all, customers only their own.
 * Optional filter ?status=PENDING|PAID|OVERDUE.
 * (CRUD: Read for Payment and Billing.)
 */
@WebServlet("/payments")
public class PaymentListServlet extends BaseServlet {

    /** Shows the bills table and the money totals. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        PaymentService paymentService = getService(PaymentService.class);
        request.setAttribute("payments", paymentService.findForUser(user, request.getParameter("status")));
        request.setAttribute("usersById", getService(UserService.class).findAllAsMap());
        if (user.canAccessAdminPages()) {
            request.setAttribute("paidTotal", paymentService.totalByStatus(PaymentStatus.PAID));
            request.setAttribute("pendingTotal", paymentService.totalByStatus(PaymentStatus.PENDING));
            request.setAttribute("overdueTotal", paymentService.totalByStatus(PaymentStatus.OVERDUE));
        }
        render(request, response, "payments/payment-list");
    }
}

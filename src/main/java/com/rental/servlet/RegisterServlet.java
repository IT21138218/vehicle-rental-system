package com.rental.servlet;

import com.rental.model.Customer;
import com.rental.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /register shows the sign-up form; POST /register creates a Customer account.
 * (CRUD: Create for User Management.)
 */
@WebServlet("/register")
public class RegisterServlet extends BaseServlet {

    private static final String VIEW = "auth/register";

    /** Shows the empty registration form. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (currentUser(request) != null) {
            redirect(request, response, currentUser(request).getDashboardPath());
            return;
        }
        request.setAttribute("form", formFromRequest(request));
        render(request, response, VIEW);
    }

    /** Validates and saves the new customer, then redirects to the login page. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String password = request.getParameter("password");
        try {
            if (password == null || !password.equals(request.getParameter("confirmPassword"))) {
                throw new IllegalArgumentException("Passwords do not match");
            }
            Customer customer = getService(UserService.class).register(
                    request.getParameter("username"), password,
                    request.getParameter("name"), request.getParameter("email"));
            flashSuccess(request, "Account " + customer.getUsername() + " created. Please log in.");
            redirect(request, response, "/login");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("form", formFromRequest(request, "username", "name", "email"));
            render(request, response, VIEW);
        }
    }
}

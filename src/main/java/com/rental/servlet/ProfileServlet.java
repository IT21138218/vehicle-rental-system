package com.rental.servlet;

import com.rental.model.User;
import com.rental.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * GET /profile shows the logged-in user's details; POST /profile saves changes.
 * (CRUD: Update for User Management - any logged-in user can edit their own profile.)
 */
@WebServlet("/profile")
public class ProfileServlet extends BaseServlet {

    private static final String VIEW = "users/profile";

    /** Shows the profile form filled with the current values. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        request.setAttribute("form", Map.of("name", user.getName(), "email", user.getEmail()));
        render(request, response, VIEW);
    }

    /** Saves the new name, e-mail and optional new password. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        try {
            String newPassword = request.getParameter("newPassword");
            if (newPassword != null && !newPassword.isEmpty()
                    && !newPassword.equals(request.getParameter("confirmPassword"))) {
                throw new IllegalArgumentException("New passwords do not match");
            }
            User updated = getService(UserService.class).updateProfile(user.getId(),
                    request.getParameter("name"), request.getParameter("email"),
                    request.getParameter("currentPassword"), newPassword);
            request.getSession().setAttribute(SESSION_USER, updated);
            flashSuccess(request, "Profile updated");
            redirect(request, response, "/profile");
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", e.getMessage());
            request.setAttribute("form", formFromRequest(request, "name", "email"));
            render(request, response, VIEW);
        }
    }
}

package com.rental.servlet;

import com.rental.model.User;
import com.rental.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * GET /admin/users/edit?id=U002 shows the edit form; POST saves it.
 * (CRUD: Update for User Management, admin side.)
 */
@WebServlet("/admin/users/edit")
public class UserEditServlet extends BaseServlet {

    private static final String VIEW = "users/user-edit";

    /** Shows the form filled with the user's current values. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Optional<User> user = getService(UserService.class).findById(request.getParameter("id"));
        if (user.isEmpty()) {
            flashError(request, "User not found");
            redirect(request, response, "/admin/users");
            return;
        }
        request.setAttribute("editUser", user.get());
        request.setAttribute("form", Map.of(
                "username", user.get().getUsername(),
                "name", user.get().getName(),
                "email", user.get().getEmail()));
        render(request, response, VIEW);
    }

    /** Saves the changes, then redirects back to the list. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        UserService userService = getService(UserService.class);
        String id = request.getParameter("id");
        try {
            User updated = userService.updateByAdmin(id, request.getParameter("username"),
                    request.getParameter("name"), request.getParameter("email"),
                    request.getParameter("newPassword"));
            flashSuccess(request, "User " + updated.getId() + " updated");
            redirect(request, response, "/admin/users");
        } catch (IllegalArgumentException e) {
            Optional<User> user = userService.findById(id);
            if (user.isEmpty()) {
                flashError(request, e.getMessage());
                redirect(request, response, "/admin/users");
                return;
            }
            request.setAttribute("error", e.getMessage());
            request.setAttribute("editUser", user.get());
            request.setAttribute("form", formFromRequest(request, "username", "name", "email"));
            render(request, response, VIEW);
        }
    }
}

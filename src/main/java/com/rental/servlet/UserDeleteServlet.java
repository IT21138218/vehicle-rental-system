package com.rental.servlet;

import com.rental.model.User;
import com.rental.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * GET /admin/users/delete?id=U002 asks for confirmation; POST deletes.
 * (CRUD: Delete for User Management.)
 */
@WebServlet("/admin/users/delete")
public class UserDeleteServlet extends BaseServlet {

    /** Shows the "Are you sure?" page. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Optional<User> user = getService(UserService.class).findById(request.getParameter("id"));
        if (user.isEmpty()) {
            flashError(request, "User not found");
            redirect(request, response, "/admin/users");
            return;
        }
        request.setAttribute("deleteUser", user.get());
        render(request, response, "users/user-delete");
    }

    /** Deletes the user (if the business rules allow it) and returns to the list. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        try {
            getService(UserService.class).delete(id, currentUser(request).getId());
            flashSuccess(request, "User " + id + " deleted");
        } catch (IllegalArgumentException e) {
            flashError(request, e.getMessage());
        }
        redirect(request, response, "/admin/users");
    }
}

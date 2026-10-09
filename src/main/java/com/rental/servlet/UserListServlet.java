package com.rental.servlet;

import com.rental.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * GET /admin/users lists all users, with optional search (?q=) and role filter (?role=).
 * (CRUD: Read/Search for User Management.)
 */
@WebServlet("/admin/users")
public class UserListServlet extends BaseServlet {

    /** Shows the filtered user list. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("q");
        String role = request.getParameter("role");
        request.setAttribute("users", getService(UserService.class).search(keyword, role));
        render(request, response, "users/user-list");
    }
}

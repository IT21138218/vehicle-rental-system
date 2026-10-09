package com.rental.servlet;

import com.rental.model.User;
import com.rental.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

/**
 * GET /login shows the login form; POST /login checks the details.
 *
 * <p><b>OOP concept - Polymorphism:</b> after a successful login the browser is sent to
 * {@code user.getDashboardPath()} - an AdminUser and a Customer each return their own page.</p>
 */
@WebServlet("/login")
public class LoginServlet extends BaseServlet {

    /** Shows the login form (or the dashboard if already logged in). */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = currentUser(request);
        if (user != null) {
            redirect(request, response, user.getDashboardPath());
            return;
        }
        render(request, response, "auth/login");
    }

    /** Checks username and password, then starts a session (Post-Redirect-Get). */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        Optional<User> user = getService(AuthService.class).login(username, request.getParameter("password"));

        if (user.isEmpty()) {
            request.setAttribute("error", "Invalid username or password");
            request.setAttribute("username", username);
            render(request, response, "auth/login");
            return;
        }

        request.getSession();
        request.changeSessionId(); // new session id after login (prevents session fixation)
        request.getSession().setAttribute(SESSION_USER, user.get());
        flashSuccess(request, "Welcome, " + user.get().getName() + "!");
        redirect(request, response, user.get().getDashboardPath());
    }
}

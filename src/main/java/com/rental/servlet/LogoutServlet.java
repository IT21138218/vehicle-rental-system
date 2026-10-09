package com.rental.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * POST /logout ends the session and returns to the login page.
 * (Logout uses POST so that a link or image on another site cannot log the user out.)
 */
@WebServlet("/logout")
public class LogoutServlet extends BaseServlet {

    /** Ends the session. */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        flashSuccess(request, "You have been logged out");
        redirect(request, response, "/login");
    }

    /** A plain GET does not log out; it just goes to the login page. */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirect(request, response, "/login");
    }
}

package com.rental.servlet;

import com.rental.model.User;
import com.rental.util.FlashUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Parent class of every servlet in the project. It holds the small helpers
 * that all servlets need, so the servlets themselves stay short.
 *
 * <p><b>OOP concepts - Inheritance + Abstraction:</b> every servlet extends this abstract
 * class and reuses its methods instead of repeating the same code.</p>
 */
public abstract class BaseServlet extends HttpServlet {

    /** Session attribute that holds the logged-in {@link User}. */
    public static final String SESSION_USER = "currentUser";

    /**
     * Returns the shared service object created by {@link AppContextListener}.
     *
     * @param type the service class, e.g. {@code UserService.class}
     * @return the single shared instance
     */
    protected <T> T getService(Class<T> type) {
        return type.cast(getServletContext().getAttribute(type.getName()));
    }

    /**
     * @return the logged-in user, or null if nobody is logged in
     */
    protected User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute(SESSION_USER);
    }

    /**
     * Shows a JSP from /WEB-INF/views (users cannot open those files directly).
     *
     * @param view path inside /WEB-INF/views without ".jsp", e.g. "users/user-list"
     */
    protected void render(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/" + view + ".jsp").forward(request, response);
    }

    /**
     * Sends the browser to another page of this application (the "Redirect" in Post-Redirect-Get).
     *
     * @param path a path such as "/admin/users"
     */
    protected void redirect(HttpServletRequest request, HttpServletResponse response, String path)
            throws IOException {
        response.sendRedirect(request.getContextPath() + path);
    }

    /** Shows a success message on the next page. */
    protected void flashSuccess(HttpServletRequest request, String message) {
        FlashUtil.success(request, message);
    }

    /** Shows an error message on the next page. */
    protected void flashError(HttpServletRequest request, String message) {
        FlashUtil.error(request, message);
    }

    /**
     * Copies request parameters into a map so a form can be filled again after an error.
     *
     * @param names the input names to copy
     * @return map of input name to typed value (missing values become "")
     */
    protected Map<String, String> formFromRequest(HttpServletRequest request, String... names) {
        Map<String, String> form = new HashMap<>();
        for (String name : names) {
            String value = request.getParameter(name);
            form.put(name, value == null ? "" : value);
        }
        return form;
    }
}

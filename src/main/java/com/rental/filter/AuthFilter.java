package com.rental.filter;

import com.rental.model.User;
import com.rental.service.UserService;
import com.rental.servlet.BaseServlet;
import com.rental.util.FlashUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

/**
 * Runs before every request and enforces security:
 *
 * <ol>
 *   <li>Public pages (login, register, CSS, JS) are always allowed.</li>
 *   <li>Every other page needs a logged-in user, otherwise the browser goes to /login.</li>
 *   <li>Pages under /admin/ also need {@code user.canAccessAdminPages()}; otherwise HTTP 403.</li>
 * </ol>
 *
 * <p><b>OOP concept - Polymorphism:</b> the filter never asks "is this an AdminUser?"
 * ({@code instanceof}). It calls {@code canAccessAdminPages()} and the real object
 * (AdminUser or Customer) gives its own answer.</p>
 */
@WebFilter("/*")
public class AuthFilter implements Filter {

    private static final String[] PUBLIC_PREFIXES = {"/login", "/register", "/css/", "/js/", "/index.jsp"};

    /** Checks login and role before letting the request reach a servlet. */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getRequestURI().substring(request.getContextPath().length());

        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        User user = loggedInUser(request);
        if (user == null) {
            FlashUtil.error(request, "Please log in to continue");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (path.startsWith("/admin/") && !user.canAccessAdminPages()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Do not let the browser cache private pages (the Back button after logout shows nothing)
        response.setHeader("Cache-Control", "no-store");
        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        if (path.isEmpty() || path.equals("/")) {
            return true;
        }
        for (String prefix : PUBLIC_PREFIXES) {
            if (path.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the logged-in user, re-read from the data file so that a deleted account
     * is logged out straight away and profile changes show immediately.
     */
    private User loggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(BaseServlet.SESSION_USER) == null) {
            return null;
        }
        User sessionUser = (User) session.getAttribute(BaseServlet.SESSION_USER);
        UserService userService = (UserService) request.getServletContext()
                .getAttribute(UserService.class.getName());
        Optional<User> fresh = userService.findById(sessionUser.getId());
        if (fresh.isEmpty()) {
            session.invalidate();
            return null;
        }
        session.setAttribute(BaseServlet.SESSION_USER, fresh.get());
        return fresh.get();
    }
}

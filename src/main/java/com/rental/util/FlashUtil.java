package com.rental.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Flash messages: a message saved in the session before a redirect and shown once
 * on the next page (then removed by header.jsp).
 *
 * <p>This is what makes Post-Redirect-Get friendly: after "Vehicle saved" the browser is
 * redirected to the list page, and the message still appears there.</p>
 */
public final class FlashUtil {

    /** Session attribute holding a success message. */
    public static final String SUCCESS = "flashSuccess";
    /** Session attribute holding an error message. */
    public static final String ERROR = "flashError";

    private FlashUtil() {
    }

    /** Stores a green success message for the next page. */
    public static void success(HttpServletRequest request, String message) {
        request.getSession().setAttribute(SUCCESS, message);
    }

    /** Stores a red error message for the next page. */
    public static void error(HttpServletRequest request, String message) {
        request.getSession().setAttribute(ERROR, message);
    }
}

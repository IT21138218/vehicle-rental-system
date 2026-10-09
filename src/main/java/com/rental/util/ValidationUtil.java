package com.rental.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Small, reusable validation checks used by model setters and services.
 *
 * <p>Every method throws {@link IllegalArgumentException} with a friendly message that
 * can be shown directly to the user (for example "Brand is required").</p>
 *
 * <p>Text values may not contain commas because the data files are comma-separated.</p>
 */
public final class ValidationUtil {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private ValidationUtil() {
    }

    /**
     * Checks a required text value.
     *
     * @param value     the value to check
     * @param fieldName name shown in the error message
     * @param maxLength the longest allowed length
     * @return the value with surrounding spaces removed
     */
    public static String requireText(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        String trimmed = value.trim();
        if (trimmed.contains(",")) {
            throw new IllegalArgumentException(fieldName + " cannot contain commas");
        }
        if (trimmed.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must be at most " + maxLength + " characters");
        }
        return trimmed;
    }

    /**
     * Checks an e-mail address.
     *
     * @param value the e-mail to check
     * @return the trimmed, lower-case e-mail
     */
    public static String requireEmail(String value) {
        String email = requireText(value, "Email", 100).toLowerCase();
        if (!EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("Email address is not valid");
        }
        return email;
    }

    /**
     * Checks that a whole number is inside a range (both ends included).
     *
     * @return the same value
     */
    public static int requireRange(int value, int min, int max, String fieldName) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max);
        }
        return value;
    }

    /**
     * Checks that an amount is greater than zero.
     *
     * @return the same value
     */
    public static double requirePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be greater than 0");
        }
        return value;
    }

    /**
     * Converts form text to a whole number.
     *
     * @throws IllegalArgumentException if the text is empty or not a number
     */
    public static int parseInt(String text, String fieldName) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException(fieldName + " must be a whole number");
        }
    }

    /**
     * Converts form text to a decimal number.
     *
     * @throws IllegalArgumentException if the text is empty or not a number
     */
    public static double parseDouble(String text, String fieldName) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException(fieldName + " must be a number");
        }
    }

    /**
     * Converts form text in the format yyyy-MM-dd to a date.
     *
     * @throws IllegalArgumentException if the text is empty or not a valid date
     */
    public static LocalDate parseDate(String text, String fieldName) {
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid date (yyyy-MM-dd)");
        }
    }
}

package com.rental.model;

import com.rental.util.ValidationUtil;

import java.util.regex.Pattern;

/**
 * A person who can log in to the system.
 *
 * <p><b>OOP concepts:</b></p>
 * <ul>
 *   <li><b>Abstraction:</b> {@code User} is abstract - you can only create an
 *       {@link AdminUser} or a {@link Customer}.</li>
 *   <li><b>Encapsulation:</b> all fields are private and every setter validates its value.</li>
 *   <li><b>Polymorphism:</b> {@link #getRole()}, {@link #canModifyVehicles()},
 *       {@link #canAccessAdminPages()} and {@link #getDashboardPath()} are answered
 *       differently by each subclass, so the rest of the code never needs {@code instanceof}.</li>
 * </ul>
 *
 * <p>File format (users.txt): {@code id,role,username,password,name,email}</p>
 */
public abstract class User implements Storable {

    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9_.]{4,20}$");

    private final String id;
    private String username;
    private String password;
    private String name;
    private String email;

    /**
     * Creates a user; every value is checked by its setter.
     *
     * <p><b>OOP concept - Encapsulation:</b> the constructor reuses the validating setters,
     * so an invalid User object can never exist.</p>
     */
    protected User(String id, String username, String password, String name, String email) {
        this.id = ValidationUtil.requireText(id, "User id", 10);
        setUsername(username);
        setPassword(password);
        setName(name);
        setEmail(email);
    }

    /**
     * Factory method: creates the correct subclass for a role.
     *
     * @param role "ADMIN" or "CUSTOMER"
     * @return an AdminUser or a Customer
     * @throws IllegalArgumentException for an unknown role
     */
    public static User create(String role, String id, String username, String password,
                              String name, String email) {
        switch (role.trim().toUpperCase()) {
            case AdminUser.ROLE:
                return new AdminUser(id, username, password, name, email);
            case Customer.ROLE:
                return new Customer(id, username, password, name, email);
            default:
                throw new IllegalArgumentException("Unknown user role: " + role);
        }
    }

    // ----- Abstract (polymorphic) methods -----

    /**
     * @return the role written to the file, e.g. "ADMIN"
     */
    public abstract String getRole();

    /**
     * <b>Polymorphism:</b> may this user add, edit or delete vehicles?
     *
     * @return true for admins, false for customers
     */
    public abstract boolean canModifyVehicles();

    /**
     * <b>Polymorphism:</b> may this user open pages under /admin/? Used by the AuthFilter.
     *
     * @return true for admins, false for customers
     */
    public abstract boolean canAccessAdminPages();

    /**
     * <b>Polymorphism:</b> where to send this user after login.
     *
     * @return a path such as "/admin/dashboard"
     */
    public abstract String getDashboardPath();

    // ----- Storable -----

    /**
     * {@inheritDoc}
     * <p>Uses {@link #getRole()}, so the correct role is written for each subclass.</p>
     */
    @Override
    public String toFileString() {
        return String.join(SEPARATOR, id, getRole(), username, password, name, email);
    }

    /**
     * Checks a password typed at login.
     *
     * @param candidate the password entered
     * @return true if it matches
     */
    public boolean checkPassword(String candidate) {
        return password.equals(candidate);
    }

    // ----- Getters and validating setters (Encapsulation) -----

    @Override
    public String getId() {
        return id;
    }

    /** @return the username (Encapsulation: read-only access to a private field) */
    public String getUsername() {
        return username;
    }

    /**
     * @param username 4-20 letters, digits, dots or underscores
     */
    public void setUsername(String username) {
        String value = ValidationUtil.requireText(username, "Username", 20);
        if (!USERNAME.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "Username must be 4-20 characters: letters, digits, dot or underscore");
        }
        this.username = value;
    }

    /**
     * There is deliberately no getPassword(): other classes can only check a password
     * with {@link #checkPassword(String)} (information hiding).
     *
     * @param password 6-30 characters, no commas
     */
    public void setPassword(String password) {
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        this.password = ValidationUtil.requireText(password, "Password", 30);
    }

    /** @return the name (Encapsulation: read-only access to a private field) */
    public String getName() {
        return name;
    }

    /** Sets the name (Encapsulation: the only way to change this private field). */
    public void setName(String name) {
        this.name = ValidationUtil.requireText(name, "Name", 60);
    }

    /** @return the email (Encapsulation: read-only access to a private field) */
    public String getEmail() {
        return email;
    }

    /** Sets the email (Encapsulation: the only way to change this private field). */
    public void setEmail(String email) {
        this.email = ValidationUtil.requireEmail(email);
    }
}

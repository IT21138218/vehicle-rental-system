package com.rental.model;

/**
 * An administrator with full access to every module.
 *
 * <p><b>OOP concepts - Inheritance + Polymorphism:</b> inherits all fields and validation
 * from {@link User} and overrides the role methods to grant full access.</p>
 */
public class AdminUser extends User {

    /** Role name stored in users.txt. */
    public static final String ROLE = "ADMIN";

    /**
     * Creates an administrator. Validation happens in {@link User}.
     */
    public AdminUser(String id, String username, String password, String name, String email) {
        super(id, username, password, name, email);
    }

    /** {@inheritDoc} */
    @Override
    public String getRole() {
        return ROLE;
    }

    /** {@inheritDoc} Admins manage the fleet. */
    @Override
    public boolean canModifyVehicles() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canAccessAdminPages() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String getDashboardPath() {
        return "/admin/dashboard";
    }
}

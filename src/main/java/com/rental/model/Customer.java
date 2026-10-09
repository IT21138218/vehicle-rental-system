package com.rental.model;

/**
 * A customer who rents vehicles, pays bills and writes reviews.
 *
 * <p><b>OOP concepts - Inheritance + Polymorphism:</b> inherits from {@link User} and
 * overrides the role methods to give limited access.</p>
 */
public class Customer extends User {

    /** Role name stored in users.txt. */
    public static final String ROLE = "CUSTOMER";

    /**
     * Creates a customer. Validation happens in {@link User}.
     */
    public Customer(String id, String username, String password, String name, String email) {
        super(id, username, password, name, email);
    }

    /** {@inheritDoc} */
    @Override
    public String getRole() {
        return ROLE;
    }

    /** {@inheritDoc} Customers can only browse vehicles. */
    @Override
    public boolean canModifyVehicles() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canAccessAdminPages() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String getDashboardPath() {
        return "/customer/dashboard";
    }
}

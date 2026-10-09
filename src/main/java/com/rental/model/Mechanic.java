package com.rental.model;

import com.rental.util.ValidationUtil;

/**
 * A mechanic who services the fleet. Extra field: area of specialization.
 *
 * <p><b>OOP concepts - Inheritance + Polymorphism:</b> extends {@link Staff};
 * mechanics get a fixed Rs. 5,000 monthly tool allowance (if they worked that month).</p>
 */
public class Mechanic extends Staff {

    /** Type name stored in drivers.txt. */
    public static final String TYPE = "MECHANIC";

    /** Fixed monthly tool allowance. */
    public static final double TOOL_ALLOWANCE = 5000.0;

    private String specialization;

    /**
     * @param specialization e.g. "Engine", "Electrical", "Body work"
     */
    public Mechanic(String id, String name, String phone, double dailyWage, String specialization) {
        super(id, name, phone, dailyWage);
        setSpecialization(specialization);
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    public String displayRole() {
        return "Mechanic (" + specialization + " specialist)";
    }

    /**
     * {@inheritDoc}
     * <p>Rule: daily wage x working days + Rs. 5,000 tool allowance (only if days &gt; 0).</p>
     */
    @Override
    public double calculateMonthlyPay(int workingDays) {
        int days = checkWorkingDays(workingDays);
        return getDailyWage() * days + (days > 0 ? TOOL_ALLOWANCE : 0);
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return TYPE + SEPARATOR + commonFileFields() + SEPARATOR + specialization;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtraLabel() {
        return "Specialization";
    }

    /** {@inheritDoc} */
    @Override
    public String getExtraValue() {
        return specialization;
    }

    /** {@inheritDoc} */
    @Override
    public void setExtraValue(String value) {
        setSpecialization(value);
    }

    /** @return the specialization (Encapsulation: read-only access to a private field) */
    public String getSpecialization() {
        return specialization;
    }

    /** Sets the specialization (Encapsulation: the only way to change this private field). */
    public void setSpecialization(String specialization) {
        this.specialization = ValidationUtil.requireText(specialization, "Specialization", 30);
    }
}

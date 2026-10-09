package com.rental.model;

import com.rental.util.ValidationUtil;

import java.util.Locale;

/**
 * An employee of the rental company.
 *
 * <p><b>OOP concepts:</b></p>
 * <ul>
 *   <li><b>Abstraction:</b> {@code Staff} is abstract - only {@link Driver} and
 *       {@link Mechanic} objects exist.</li>
 *   <li><b>Encapsulation:</b> private fields; the name cannot be blank, the phone must be a
 *       10-digit number starting with 0 and the daily wage must be greater than 0.</li>
 *   <li><b>Polymorphism:</b> {@link #displayRole()} and {@link #calculateMonthlyPay(int)}
 *       are different for drivers and mechanics.</li>
 * </ul>
 *
 * <p>File format (drivers.txt): {@code type,id,name,phone,dailyWage,extra}</p>
 */
public abstract class Staff implements Storable {

    private final String id;
    private String name;
    private String phone;
    private double dailyWage;

    /**
     * Creates a staff member; every value is validated (Encapsulation).
     */
    protected Staff(String id, String name, String phone, double dailyWage) {
        this.id = ValidationUtil.requireText(id, "Staff id", 10);
        setName(name);
        setPhone(phone);
        setDailyWage(dailyWage);
    }

    // ----- Abstract (polymorphic) methods -----

    /**
     * @return "DRIVER" or "MECHANIC" - the first field in drivers.txt
     */
    public abstract String getType();

    /**
     * <b>Polymorphism:</b> a description of the job, different for each type.
     *
     * @return e.g. "Driver (licence B1234567)"
     */
    public abstract String displayRole();

    /**
     * <b>Polymorphism:</b> monthly pay = wage x days plus the type's own allowance.
     *
     * @param workingDays days worked in the month (0-31)
     * @return pay in rupees
     */
    public abstract double calculateMonthlyPay(int workingDays);

    /**
     * @return label of the type-specific field, e.g. "Licence number"
     */
    public abstract String getExtraLabel();

    /**
     * @return value of the type-specific field
     */
    public abstract String getExtraValue();

    /**
     * <b>Polymorphism:</b> sets the type-specific field with that type's own validation.
     */
    public abstract void setExtraValue(String value);

    // ----- Helpers for subclasses -----

    /**
     * The fields every staff member writes, in file order.
     *
     * @return "id,name,phone,dailyWage"
     */
    protected String commonFileFields() {
        return String.join(SEPARATOR, id, name, phone, String.format(Locale.ROOT, "%.2f", dailyWage));
    }

    /**
     * Checks the number of working days used for pay.
     */
    protected static int checkWorkingDays(int workingDays) {
        return ValidationUtil.requireRange(workingDays, 0, 31, "Working days");
    }

    // ----- Getters and validating setters (Encapsulation) -----

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = ValidationUtil.requireText(name, "Name", 60);
    }

    public String getPhone() {
        return phone;
    }

    /**
     * @param phone 10 digits starting with 0, e.g. 0771234567
     */
    public void setPhone(String phone) {
        String value = ValidationUtil.requireText(phone, "Phone", 10);
        if (!value.matches("0\\d{9}")) {
            throw new IllegalArgumentException("Phone must be 10 digits starting with 0, e.g. 0771234567");
        }
        this.phone = value;
    }

    public double getDailyWage() {
        return dailyWage;
    }

    /**
     * @param dailyWage greater than 0 and at most Rs. 100,000
     */
    public void setDailyWage(double dailyWage) {
        ValidationUtil.requirePositive(dailyWage, "Daily wage");
        if (dailyWage > 100_000) {
            throw new IllegalArgumentException("Daily wage must be at most 100,000");
        }
        this.dailyWage = Math.round(dailyWage * 100.0) / 100.0;
    }
}

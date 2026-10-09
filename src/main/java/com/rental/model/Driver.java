package com.rental.model;

/**
 * A chauffeur who can be hired with a vehicle. Extra field: driving licence number.
 *
 * <p><b>OOP concepts - Inheritance + Polymorphism:</b> extends {@link Staff};
 * drivers get a Rs. 500 meal allowance for every working day.</p>
 */
public class Driver extends Staff {

    /** Type name stored in drivers.txt. */
    public static final String TYPE = "DRIVER";

    /** Meal allowance per working day. */
    public static final double DAILY_ALLOWANCE = 500.0;

    private String licenseNumber;

    /**
     * @param licenseNumber one capital letter followed by 7 digits, e.g. B1234567
     */
    public Driver(String id, String name, String phone, double dailyWage, String licenseNumber) {
        super(id, name, phone, dailyWage);
        setLicenseNumber(licenseNumber);
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return TYPE;
    }

    /** {@inheritDoc} */
    @Override
    public String displayRole() {
        return "Driver (licence " + licenseNumber + ")";
    }

    /**
     * {@inheritDoc}
     * <p>Rule: (daily wage + Rs. 500 allowance) x working days.</p>
     */
    @Override
    public double calculateMonthlyPay(int workingDays) {
        return (getDailyWage() + DAILY_ALLOWANCE) * checkWorkingDays(workingDays);
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return TYPE + SEPARATOR + commonFileFields() + SEPARATOR + licenseNumber;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtraLabel() {
        return "Licence number";
    }

    /** {@inheritDoc} */
    @Override
    public String getExtraValue() {
        return licenseNumber;
    }

    /** {@inheritDoc} */
    @Override
    public void setExtraValue(String value) {
        setLicenseNumber(value);
    }

    /** @return the license number (Encapsulation: read-only access to a private field) */
    public String getLicenseNumber() {
        return licenseNumber;
    }

    /**
     * @param licenseNumber e.g. B1234567 (stored in capitals)
     */
    public void setLicenseNumber(String licenseNumber) {
        String value = licenseNumber == null ? "" : licenseNumber.trim().toUpperCase();
        if (!value.matches("[A-Z]\\d{7}")) {
            throw new IllegalArgumentException("Licence number must be one letter and 7 digits, e.g. B1234567");
        }
        this.licenseNumber = value;
    }
}

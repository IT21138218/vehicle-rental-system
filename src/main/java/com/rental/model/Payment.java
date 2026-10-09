package com.rental.model;

import com.rental.util.ValidationUtil;

import java.time.LocalDate;
import java.util.Locale;

/**
 * A bill for one rental.
 *
 * <p>The base amount is fixed when the bill is generated (days x the vehicle's daily rate).
 * Each payment method then adds its own charges.</p>
 *
 * <p><b>OOP concepts:</b></p>
 * <ul>
 *   <li><b>Abstraction:</b> {@code Payment} is abstract - only {@link CashPayment} and
 *       {@link CardPayment} objects exist.</li>
 *   <li><b>Encapsulation:</b> private fields; setters reject a base amount of 0 or less,
 *       negative late days, or a missing status.</li>
 *   <li><b>Polymorphism:</b> {@link #calculateTotal()}, {@link #getLateFee()} and
 *       {@link #getSurcharge()} are different for cash and card, and the bill page simply calls
 *       them on a {@code Payment} reference.</li>
 * </ul>
 *
 * <p>File format (payments.txt):
 * {@code method,id,rentalId,customerId,issueDate,baseAmount,lateDays,status,extra}</p>
 */
public abstract class Payment implements Storable {

    /** Highest number of late days that can be recorded. */
    public static final int MAX_LATE_DAYS = 60;

    private final String id;
    private final String rentalId;
    private final String customerId;
    private final LocalDate issueDate;
    private double baseAmount;
    private int lateDays;
    private PaymentStatus status;

    /**
     * Creates a payment; each value is validated (Encapsulation).
     */
    protected Payment(String id, String rentalId, String customerId, LocalDate issueDate,
                      double baseAmount, int lateDays, PaymentStatus status) {
        this.id = ValidationUtil.requireText(id, "Payment id", 10);
        this.rentalId = ValidationUtil.requireText(rentalId, "Rental id", 10);
        this.customerId = ValidationUtil.requireText(customerId, "Customer id", 10);
        if (issueDate == null) {
            throw new IllegalArgumentException("Issue date is required");
        }
        this.issueDate = issueDate;
        setBaseAmount(baseAmount);
        setLateDays(lateDays);
        setStatus(status);
    }

    // ----- Abstract (polymorphic) methods -----

    /**
     * @return "CASH" or "CARD" - the first field in payments.txt
     */
    public abstract String getMethod();

    /**
     * <b>Polymorphism:</b> the late fee, using this method's own fee per late day.
     *
     * @return late fee in rupees
     */
    public abstract double getLateFee();

    /**
     * <b>Polymorphism:</b> the amount the customer must pay, using this method's own rules.
     *
     * @return the total in rupees
     */
    public abstract double calculateTotal();

    /**
     * @return a short description, e.g. "Card ending 4242"
     */
    public abstract String displayMethod();

    /**
     * @return label of the method-specific field, e.g. "Received by"
     */
    public abstract String getExtraLabel();

    /**
     * @return value of the method-specific field
     */
    public abstract String getExtraValue();

    /**
     * <b>Polymorphism:</b> sets the method-specific field; each subclass validates it its own way.
     */
    public abstract void setExtraValue(String value);

    /**
     * Extra charge for the payment method. Cash has none; {@link CardPayment} overrides this.
     *
     * @return surcharge in rupees
     */
    public double getSurcharge() {
        return 0.0;
    }

    // ----- Helpers for subclasses -----

    /**
     * The fields every payment writes, in file order.
     *
     * @return "id,rentalId,customerId,issueDate,baseAmount,lateDays,status"
     */
    protected String commonFileFields() {
        return String.join(SEPARATOR, id, rentalId, customerId, issueDate.toString(),
                String.format(Locale.ROOT, "%.2f", baseAmount), String.valueOf(lateDays), status.name());
    }

    /**
     * Rounds money to 2 decimal places.
     */
    protected static double roundMoney(double amount) {
        return Math.round(amount * 100.0) / 100.0;
    }

    // ----- Getters and validating setters (Encapsulation) -----

    @Override
    public String getId() {
        return id;
    }

    public String getRentalId() {
        return rentalId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    /**
     * @param baseAmount must be greater than 0
     */
    public void setBaseAmount(double baseAmount) {
        this.baseAmount = roundMoney(ValidationUtil.requirePositive(baseAmount, "Base amount"));
    }

    public int getLateDays() {
        return lateDays;
    }

    /**
     * @param lateDays 0 to {@value #MAX_LATE_DAYS}
     */
    public void setLateDays(int lateDays) {
        this.lateDays = ValidationUtil.requireRange(lateDays, 0, MAX_LATE_DAYS, "Late days");
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Payment status is required");
        }
        this.status = status;
    }
}

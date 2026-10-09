package com.rental.model;

import com.rental.util.ValidationUtil;

import java.time.LocalDate;

/**
 * A bill paid in cash at the counter. Extra field: the staff member or desk that received it.
 *
 * <p><b>OOP concepts - Inheritance + Polymorphism:</b> extends {@link Payment};
 * late fee is Rs. 1,000 per day and there is no surcharge.</p>
 */
public class CashPayment extends Payment {

    /** Method name stored in payments.txt. */
    public static final String METHOD = "CASH";

    /** Late fee per day for cash payments. */
    public static final double LATE_FEE_PER_DAY = 1000.0;

    private String receivedBy;

    /**
     * @param receivedBy who took the cash, e.g. "Front Desk"
     */
    public CashPayment(String id, String rentalId, String customerId, LocalDate issueDate,
                       double baseAmount, int lateDays, PaymentStatus status, String receivedBy) {
        super(id, rentalId, customerId, issueDate, baseAmount, lateDays, status);
        setReceivedBy(receivedBy);
    }

    /** {@inheritDoc} */
    @Override
    public String getMethod() {
        return METHOD;
    }

    /** {@inheritDoc} Rs. 1,000 for each late day. */
    @Override
    public double getLateFee() {
        return roundMoney(getLateDays() * LATE_FEE_PER_DAY);
    }

    /**
     * {@inheritDoc}
     * <p>Rule: base amount + late fee.</p>
     */
    @Override
    public double calculateTotal() {
        return roundMoney(getBaseAmount() + getLateFee());
    }

    /** {@inheritDoc} */
    @Override
    public String displayMethod() {
        return "Cash (received by " + receivedBy + ")";
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return METHOD + SEPARATOR + commonFileFields() + SEPARATOR + receivedBy;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtraLabel() {
        return "Received by";
    }

    /** {@inheritDoc} */
    @Override
    public String getExtraValue() {
        return receivedBy;
    }

    /** {@inheritDoc} */
    @Override
    public void setExtraValue(String value) {
        setReceivedBy(value);
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = ValidationUtil.requireText(receivedBy, "Received by", 40);
    }
}

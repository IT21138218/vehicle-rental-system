package com.rental.model;

import java.time.LocalDate;

/**
 * A bill paid by credit or debit card. Extra field: the last 4 digits of the card
 * (the full number is never stored).
 *
 * <p><b>OOP concepts - Inheritance + Polymorphism:</b> extends {@link Payment};
 * late fee is Rs. 1,500 per day and a 3% card processing surcharge is added.</p>
 */
public class CardPayment extends Payment {

    /** Method name stored in payments.txt. */
    public static final String METHOD = "CARD";

    /** Late fee per day for card payments. */
    public static final double LATE_FEE_PER_DAY = 1500.0;

    /** Card processing surcharge (3%). */
    public static final double SURCHARGE_RATE = 0.03;

    private String cardLast4;

    /**
     * @param cardLast4 exactly 4 digits
     */
    public CardPayment(String id, String rentalId, String customerId, LocalDate issueDate,
                       double baseAmount, int lateDays, PaymentStatus status, String cardLast4) {
        super(id, rentalId, customerId, issueDate, baseAmount, lateDays, status);
        setCardLast4(cardLast4);
    }

    /** {@inheritDoc} */
    @Override
    public String getMethod() {
        return METHOD;
    }

    /** {@inheritDoc} Rs. 1,500 for each late day. */
    @Override
    public double getLateFee() {
        return roundMoney(getLateDays() * LATE_FEE_PER_DAY);
    }

    /**
     * {@inheritDoc}
     * <p>Overridden: 3% of (base amount + late fee).</p>
     */
    @Override
    public double getSurcharge() {
        return roundMoney((getBaseAmount() + getLateFee()) * SURCHARGE_RATE);
    }

    /**
     * {@inheritDoc}
     * <p>Rule: base amount + late fee + 3% surcharge.</p>
     */
    @Override
    public double calculateTotal() {
        return roundMoney(getBaseAmount() + getLateFee() + getSurcharge());
    }

    /** {@inheritDoc} */
    @Override
    public String displayMethod() {
        return "Card ending " + cardLast4;
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return METHOD + SEPARATOR + commonFileFields() + SEPARATOR + cardLast4;
    }

    /** {@inheritDoc} */
    @Override
    public String getExtraLabel() {
        return "Card last 4 digits";
    }

    /** {@inheritDoc} */
    @Override
    public String getExtraValue() {
        return cardLast4;
    }

    /** {@inheritDoc} */
    @Override
    public void setExtraValue(String value) {
        setCardLast4(value);
    }

    public String getCardLast4() {
        return cardLast4;
    }

    /**
     * @param cardLast4 exactly 4 digits, e.g. "4242"
     */
    public void setCardLast4(String cardLast4) {
        if (cardLast4 == null || !cardLast4.trim().matches("\\d{4}")) {
            throw new IllegalArgumentException("Card last 4 digits must be exactly 4 numbers");
        }
        this.cardLast4 = cardLast4.trim();
    }
}

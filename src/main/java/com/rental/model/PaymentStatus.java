package com.rental.model;

/**
 * The state of a bill.
 *
 * <ul>
 *   <li>{@link #PENDING} - created, waiting for payment</li>
 *   <li>{@link #PAID} - settled</li>
 *   <li>{@link #OVERDUE} - not paid in time</li>
 * </ul>
 */
public enum PaymentStatus {
    /** Bill created, waiting for payment. */
    PENDING,
    /** Bill settled. */
    PAID,
    /** Bill not paid in time. */
    OVERDUE
}

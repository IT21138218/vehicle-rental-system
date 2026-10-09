package com.rental.model;

/**
 * The life cycle of a rental.
 *
 * <ul>
 *   <li>{@link #ACTIVE} - booked (upcoming or currently on the road)</li>
 *   <li>{@link #RETURNED} - the vehicle came back; the rental is finished</li>
 *   <li>{@link #CANCELLED} - the booking was cancelled before it finished</li>
 * </ul>
 *
 * <p>An enum is safer than plain text: only these three values can ever exist.</p>
 */
public enum RentalStatus {
    /** Booked: upcoming or currently on the road. */
    ACTIVE,
    /** The vehicle came back; the rental is finished. */
    RETURNED,
    /** Cancelled before it finished; dates are free again. */
    CANCELLED
}

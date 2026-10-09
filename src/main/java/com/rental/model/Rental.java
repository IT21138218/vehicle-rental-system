package com.rental.model;

import com.rental.util.ValidationUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * A booking of one vehicle by one customer for a date range.
 *
 * <p>Dates work like hotel nights: a rental from 20 Oct to 25 Oct is 5 days, and the
 * vehicle is free again on 25 Oct (so another booking may start that day).</p>
 *
 * <p><b>OOP concepts:</b></p>
 * <ul>
 *   <li><b>Encapsulation:</b> private fields; {@link #setDates} refuses an end date that is
 *       not after the start date, so an invalid rental cannot exist.</li>
 *   <li><b>Abstraction:</b> other classes ask {@link #overlaps} instead of comparing dates themselves.</li>
 * </ul>
 *
 * <p>File format (rentals.txt): {@code id,customerId,vehicleId,startDate,endDate,status}</p>
 */
public class Rental implements Storable {

    private final String id;
    private final String customerId;
    private final String vehicleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private RentalStatus status;

    /**
     * Creates a rental; dates and status are validated.
     */
    public Rental(String id, String customerId, String vehicleId,
                  LocalDate startDate, LocalDate endDate, RentalStatus status) {
        this.id = ValidationUtil.requireText(id, "Rental id", 10);
        this.customerId = ValidationUtil.requireText(customerId, "Customer id", 10);
        this.vehicleId = ValidationUtil.requireText(vehicleId, "Vehicle id", 10);
        setDates(startDate, endDate);
        setStatus(status);
    }

    /**
     * Number of days charged: the days between start and end.
     *
     * @return at least 1
     */
    public long getDays() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * Checks whether this ACTIVE rental clashes with another date range.
     * Two ranges overlap when each one starts before the other one ends.
     *
     * @param otherStart start of the other range
     * @param otherEnd   end of the other range
     * @return true if this rental is ACTIVE and the dates overlap
     */
    public boolean overlaps(LocalDate otherStart, LocalDate otherEnd) {
        return status == RentalStatus.ACTIVE
                && otherStart.isBefore(endDate)
                && otherEnd.isAfter(startDate);
    }

    /**
     * @return true if the rental is still booked (not returned or cancelled)
     */
    public boolean isActive() {
        return status == RentalStatus.ACTIVE;
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return String.join(SEPARATOR, id, customerId, vehicleId,
                startDate.toString(), endDate.toString(), status.name());
    }

    // ----- Getters and validating setters (Encapsulation) -----

    @Override
    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Sets both dates together, so they can be checked against each other.
     *
     * @throws IllegalArgumentException if a date is missing or the end is not after the start
     */
    public void setDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and end dates are required");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after the start date");
        }
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public void setStatus(RentalStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Rental status is required");
        }
        this.status = status;
    }
}

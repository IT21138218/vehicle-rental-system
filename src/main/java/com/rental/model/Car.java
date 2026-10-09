package com.rental.model;

import com.rental.util.ValidationUtil;

/**
 * A car. Extra field: number of seats.
 *
 * <p><b>OOP concepts - Inheritance + Polymorphism:</b> extends {@link Vehicle} and
 * overrides {@link #calculateDailyRate()}: cars with more than 5 seats cost 10% more.</p>
 */
public class Car extends Vehicle {

    /** Type name stored in vehicles.txt. */
    public static final String TYPE = "CAR";

    private int seats;

    /**
     * @param seats 2 to 9 seats
     */
    public Car(String id, String brand, String model, int year, double baseDailyRate,
               boolean available, int seats) {
        super(id, brand, model, year, baseDailyRate, available);
        setSeats(seats);
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * {@inheritDoc}
     * <p>Rule: more than 5 seats adds a 10% surcharge.</p>
     */
    @Override
    public double calculateDailyRate() {
        double rate = getBaseDailyRate();
        if (seats > 5) {
            rate = rate * 1.10;
        }
        return roundMoney(rate);
    }

    /** {@inheritDoc} */
    @Override
    public String displayDetails() {
        return getBrand() + " " + getModel() + " (" + getYear() + ") - Car with " + seats + " seats";
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return TYPE + SEPARATOR + commonFileFields() + SEPARATOR + seats;
    }

    /** {@inheritDoc} */
    @Override
    public String getSpecLabel() {
        return "Seats";
    }

    /** {@inheritDoc} */
    @Override
    public int getSpecValue() {
        return seats;
    }

    /** {@inheritDoc} */
    @Override
    public void setSpecValue(int value) {
        setSeats(value);
    }

    /** @return the seats (Encapsulation: read-only access to a private field) */
    public int getSeats() {
        return seats;
    }

    /**
     * @param seats 2 to 9
     */
    public void setSeats(int seats) {
        this.seats = ValidationUtil.requireRange(seats, 2, 9, "Seats");
    }
}

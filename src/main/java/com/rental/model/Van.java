package com.rental.model;

import com.rental.util.ValidationUtil;

/**
 * A van. Extra field: cargo capacity in kilograms.
 *
 * <p><b>OOP concepts - Inheritance + Polymorphism:</b> extends {@link Vehicle} and
 * overrides {@link #calculateDailyRate()}: Rs. 2 is added per kg of cargo capacity.</p>
 */
public class Van extends Vehicle {

    /** Type name stored in vehicles.txt. */
    public static final String TYPE = "VAN";

    /** Extra charge per kg of cargo capacity, per day. */
    public static final double RATE_PER_KG = 2.0;

    private int cargoCapacity;

    /**
     * @param cargoCapacity 100 to 5000 kg
     */
    public Van(String id, String brand, String model, int year, double baseDailyRate,
               boolean available, int cargoCapacity) {
        super(id, brand, model, year, baseDailyRate, available);
        setCargoCapacity(cargoCapacity);
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * {@inheritDoc}
     * <p>Rule: base rate + Rs. 2 for every kg of cargo capacity.</p>
     */
    @Override
    public double calculateDailyRate() {
        return roundMoney(getBaseDailyRate() + cargoCapacity * RATE_PER_KG);
    }

    /** {@inheritDoc} */
    @Override
    public String displayDetails() {
        return getBrand() + " " + getModel() + " (" + getYear() + ") - Van carrying " + cargoCapacity + " kg";
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return TYPE + SEPARATOR + commonFileFields() + SEPARATOR + cargoCapacity;
    }

    /** {@inheritDoc} */
    @Override
    public String getSpecLabel() {
        return "Cargo (kg)";
    }

    /** {@inheritDoc} */
    @Override
    public int getSpecValue() {
        return cargoCapacity;
    }

    /** {@inheritDoc} */
    @Override
    public void setSpecValue(int value) {
        setCargoCapacity(value);
    }

    public int getCargoCapacity() {
        return cargoCapacity;
    }

    /**
     * @param cargoCapacity 100 to 5000 kg
     */
    public void setCargoCapacity(int cargoCapacity) {
        this.cargoCapacity = ValidationUtil.requireRange(cargoCapacity, 100, 5000, "Cargo capacity");
    }
}

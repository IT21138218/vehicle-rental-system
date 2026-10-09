package com.rental.model;

import com.rental.util.ValidationUtil;

/**
 * A motorbike. Extra field: engine size in CC.
 *
 * <p><b>OOP concepts - Inheritance + Polymorphism:</b> extends {@link Vehicle} and
 * overrides {@link #calculateDailyRate()}: bikes above 500cc cost 20% more.</p>
 */
public class Bike extends Vehicle {

    /** Type name stored in vehicles.txt. */
    public static final String TYPE = "BIKE";

    private int engineCC;

    /**
     * @param engineCC 50 to 2000 cc
     */
    public Bike(String id, String brand, String model, int year, double baseDailyRate,
                boolean available, int engineCC) {
        super(id, brand, model, year, baseDailyRate, available);
        setEngineCC(engineCC);
    }

    /** {@inheritDoc} */
    @Override
    public String getType() {
        return TYPE;
    }

    /**
     * {@inheritDoc}
     * <p>Rule: engines above 500cc are multiplied by 1.2 (20% more).</p>
     */
    @Override
    public double calculateDailyRate() {
        double rate = getBaseDailyRate();
        if (engineCC > 500) {
            rate = rate * 1.2;
        }
        return roundMoney(rate);
    }

    /** {@inheritDoc} */
    @Override
    public String displayDetails() {
        return getBrand() + " " + getModel() + " (" + getYear() + ") - " + engineCC + "cc Bike";
    }

    /** {@inheritDoc} */
    @Override
    public String toFileString() {
        return TYPE + SEPARATOR + commonFileFields() + SEPARATOR + engineCC;
    }

    /** {@inheritDoc} */
    @Override
    public String getSpecLabel() {
        return "Engine (cc)";
    }

    /** {@inheritDoc} */
    @Override
    public int getSpecValue() {
        return engineCC;
    }

    /** {@inheritDoc} */
    @Override
    public void setSpecValue(int value) {
        setEngineCC(value);
    }

    public int getEngineCC() {
        return engineCC;
    }

    /**
     * @param engineCC 50 to 2000
     */
    public void setEngineCC(int engineCC) {
        this.engineCC = ValidationUtil.requireRange(engineCC, 50, 2000, "Engine CC");
    }
}

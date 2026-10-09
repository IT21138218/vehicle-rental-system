package com.rental.model;

import com.rental.util.ValidationUtil;

import java.time.Year;
import java.util.Locale;

/**
 * A vehicle in the rental fleet.
 *
 * <p><b>OOP concepts:</b></p>
 * <ul>
 *   <li><b>Abstraction:</b> {@code Vehicle} is abstract - only {@link Car}, {@link Bike}
 *       and {@link Van} objects can be created.</li>
 *   <li><b>Encapsulation:</b> private fields; setters reject bad values
 *       (blank brand, year out of range, rate not greater than 0 ...).</li>
 *   <li><b>Inheritance:</b> common fields live here once and are inherited by every subclass.</li>
 *   <li><b>Polymorphism:</b> {@link #calculateDailyRate()}, {@link #displayDetails()} and
 *       {@link #toFileString()} are overridden, so a {@code List<Vehicle>} can hold cars,
 *       bikes and vans and each one behaves correctly.</li>
 * </ul>
 *
 * <p>File format (vehicles.txt): {@code type,id,brand,model,year,baseDailyRate,available,extra}</p>
 */
public abstract class Vehicle implements Storable {

    /** Oldest model year accepted in the fleet. */
    public static final int MIN_YEAR = 1990;

    private final String id;
    private String brand;
    private String model;
    private int year;
    private double baseDailyRate;
    private boolean available;

    /**
     * Creates a vehicle; each value is validated by its setter (Encapsulation).
     */
    protected Vehicle(String id, String brand, String model, int year, double baseDailyRate, boolean available) {
        this.id = ValidationUtil.requireText(id, "Vehicle id", 10);
        setBrand(brand);
        setModel(model);
        setYear(year);
        setBaseDailyRate(baseDailyRate);
        setAvailable(available);
    }

    // ----- Abstract (polymorphic) methods -----

    /**
     * @return "CAR", "BIKE" or "VAN" - the first field in vehicles.txt
     */
    public abstract String getType();

    /**
     * <b>Polymorphism:</b> the price per day after the subclass's own rule is applied
     * (e.g. a large car costs 10% more).
     *
     * @return the daily rate in rupees
     */
    public abstract double calculateDailyRate();

    /**
     * <b>Polymorphism:</b> a one-line description, different for each type.
     *
     * @return e.g. "Toyota Corolla (2021) - Car with 5 seats"
     */
    public abstract String displayDetails();

    /**
     * @return label of the type-specific field, e.g. "Seats"
     */
    public abstract String getSpecLabel();

    /**
     * @return value of the type-specific field (seats, engine CC or cargo kg)
     */
    public abstract int getSpecValue();

    /**
     * <b>Polymorphism:</b> sets the type-specific field; each subclass applies its own limits.
     *
     * @param value seats, engine CC or cargo capacity
     */
    public abstract void setSpecValue(int value);

    // ----- Helpers for subclasses -----

    /**
     * The fields every vehicle writes, in file order (used by each subclass's toFileString).
     *
     * @return "id,brand,model,year,baseDailyRate,available"
     */
    protected String commonFileFields() {
        return String.join(SEPARATOR, id, brand, model, String.valueOf(year),
                String.format(Locale.ROOT, "%.2f", baseDailyRate), String.valueOf(available));
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

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = ValidationUtil.requireText(brand, "Brand", 30);
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = ValidationUtil.requireText(model, "Model", 30);
    }

    public int getYear() {
        return year;
    }

    /**
     * @param year between 1990 and next year
     */
    public void setYear(int year) {
        this.year = ValidationUtil.requireRange(year, MIN_YEAR, Year.now().getValue() + 1, "Year");
    }

    public double getBaseDailyRate() {
        return baseDailyRate;
    }

    /**
     * @param baseDailyRate must be greater than 0 and at most Rs. 1,000,000
     */
    public void setBaseDailyRate(double baseDailyRate) {
        ValidationUtil.requirePositive(baseDailyRate, "Daily rate");
        if (baseDailyRate > 1_000_000) {
            throw new IllegalArgumentException("Daily rate must be at most 1,000,000");
        }
        this.baseDailyRate = roundMoney(baseDailyRate);
    }

    /**
     * @return true if the admin has put this vehicle in service (it can be booked)
     */
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

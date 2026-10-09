package com.rental.repository;

import com.rental.model.Vehicle;
import com.rental.util.ValidationUtil;

import java.nio.file.Path;

/**
 * Stores vehicles in vehicles.txt.
 *
 * <p>Line format: {@code type,id,brand,model,year,baseDailyRate,available,extra}<br>
 * Example: {@code CAR,V001,Toyota,Corolla,2021,8500.00,true,5}</p>
 *
 * <p><b>OOP concepts - Inheritance + Information hiding:</b> CRUD comes from
 * {@link AbstractFileRepository}; this class only turns one line into a Car, Bike or Van.</p>
 */
public class VehicleRepository extends AbstractFileRepository<Vehicle> {

    /** Name of the data file. */
    public static final String FILE_NAME = "vehicles.txt";

    private static final int FIELD_COUNT = 8;

    /**
     * @param filePath full path of vehicles.txt
     */
    public VehicleRepository(Path filePath) {
        super(filePath);
    }

    /**
     * Factory step: field 0 ("CAR", "BIKE" or "VAN") decides which subclass is created.
     *
     * @param fields {type, id, brand, model, year, baseDailyRate, available, extra}
     */
    @Override
    protected Vehicle parse(String[] fields) {
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException("expected " + FIELD_COUNT + " fields but found " + fields.length);
        }
        return Vehicle.create(
                fields[0],
                fields[1],
                fields[2],
                fields[3],
                ValidationUtil.parseInt(fields[4], "Year"),
                ValidationUtil.parseDouble(fields[5], "Daily rate"),
                parseBoolean(fields[6]),
                ValidationUtil.parseInt(fields[7], fields[0] + " extra value"));
    }
}

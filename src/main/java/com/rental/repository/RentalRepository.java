package com.rental.repository;

import com.rental.model.Rental;
import com.rental.model.RentalStatus;
import com.rental.util.ValidationUtil;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores rentals in rentals.txt.
 *
 * <p>Line format: {@code id,customerId,vehicleId,startDate,endDate,status}<br>
 * Example: {@code R008,U004,V001,2026-10-20,2026-10-25,ACTIVE}</p>
 *
 * <p><b>OOP concepts - Inheritance + Information hiding:</b> CRUD comes from
 * {@link AbstractFileRepository}; this class only reads one rental line.</p>
 */
public class RentalRepository extends AbstractFileRepository<Rental> {

    /** Name of the data file. */
    public static final String FILE_NAME = "rentals.txt";

    private static final int FIELD_COUNT = 6;

    /**
     * @param filePath full path of rentals.txt
     */
    public RentalRepository(Path filePath) {
        super(filePath);
    }

    /**
     * Builds a Rental from {id, customerId, vehicleId, startDate, endDate, status}.
     * An unknown status (e.g. "LOST") throws, so the line is skipped as malformed.
     */
    @Override
    protected Rental parse(String[] fields) {
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException("expected " + FIELD_COUNT + " fields but found " + fields.length);
        }
        return new Rental(fields[0], fields[1], fields[2],
                ValidationUtil.parseDate(fields[3], "Start date"),
                ValidationUtil.parseDate(fields[4], "End date"),
                RentalStatus.valueOf(fields[5].trim().toUpperCase()));
    }

    /**
     * @param vehicleId a vehicle id
     * @return every rental (any status) of that vehicle
     */
    public List<Rental> findByVehicleId(String vehicleId) {
        List<Rental> result = new ArrayList<>();
        for (Rental rental : findAll()) {
            if (rental.getVehicleId().equalsIgnoreCase(vehicleId)) {
                result.add(rental);
            }
        }
        return result;
    }

    /**
     * @param customerId a user id
     * @return every rental (any status) made by that customer
     */
    public List<Rental> findByCustomerId(String customerId) {
        List<Rental> result = new ArrayList<>();
        for (Rental rental : findAll()) {
            if (rental.getCustomerId().equalsIgnoreCase(customerId)) {
                result.add(rental);
            }
        }
        return result;
    }
}

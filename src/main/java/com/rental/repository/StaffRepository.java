package com.rental.repository;

import com.rental.model.Staff;
import com.rental.util.ValidationUtil;

import java.nio.file.Path;

/**
 * Stores drivers and mechanics in drivers.txt.
 *
 * <p>Line format: {@code type,id,name,phone,dailyWage,extra}<br>
 * Example: {@code DRIVER,S001,Kamal Silva,0771234567,3500.00,B1234567}</p>
 *
 * <p><b>OOP concepts - Inheritance + Information hiding:</b> CRUD comes from
 * {@link AbstractFileRepository}; this class only turns one line into a Driver or Mechanic.</p>
 */
public class StaffRepository extends AbstractFileRepository<Staff> {

    /** Name of the data file. */
    public static final String FILE_NAME = "drivers.txt";

    private static final int FIELD_COUNT = 6;

    /**
     * @param filePath full path of drivers.txt
     */
    public StaffRepository(Path filePath) {
        super(filePath);
    }

    /**
     * Factory step: field 0 ("DRIVER" or "MECHANIC") decides which subclass is created.
     */
    @Override
    protected Staff parse(String[] fields) {
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException("expected " + FIELD_COUNT + " fields but found " + fields.length);
        }
        return Staff.create(fields[0], fields[1], fields[2], fields[3],
                ValidationUtil.parseDouble(fields[4], "Daily wage"), fields[5]);
    }
}

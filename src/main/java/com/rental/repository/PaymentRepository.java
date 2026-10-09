package com.rental.repository;

import com.rental.model.Payment;
import com.rental.model.PaymentStatus;
import com.rental.util.ValidationUtil;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Stores bills in payments.txt.
 *
 * <p>Line format: {@code method,id,rentalId,customerId,issueDate,baseAmount,lateDays,status,extra}<br>
 * Example: {@code CARD,P002,R002,U004,2026-08-12,10800.00,1,PAID,4242}</p>
 *
 * <p><b>OOP concepts - Inheritance + Information hiding:</b> CRUD comes from
 * {@link AbstractFileRepository}; this class only turns one line into a CashPayment or CardPayment.</p>
 */
public class PaymentRepository extends AbstractFileRepository<Payment> {

    /** Name of the data file. */
    public static final String FILE_NAME = "payments.txt";

    private static final int FIELD_COUNT = 9;

    /**
     * @param filePath full path of payments.txt
     */
    public PaymentRepository(Path filePath) {
        super(filePath);
    }

    /**
     * Factory step: field 0 ("CASH" or "CARD") decides which subclass is created.
     */
    @Override
    protected Payment parse(String[] fields) {
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException("expected " + FIELD_COUNT + " fields but found " + fields.length);
        }
        return Payment.create(
                fields[0],
                fields[1],
                fields[2],
                fields[3],
                ValidationUtil.parseDate(fields[4], "Issue date"),
                ValidationUtil.parseDouble(fields[5], "Base amount"),
                ValidationUtil.parseInt(fields[6], "Late days"),
                PaymentStatus.valueOf(fields[7].trim().toUpperCase()),
                fields[8]);
    }

    /**
     * @param rentalId a rental id
     * @return the bill for that rental, if one exists
     */
    public Optional<Payment> findByRentalId(String rentalId) {
        for (Payment payment : findAll()) {
            if (payment.getRentalId().equalsIgnoreCase(rentalId)) {
                return Optional.of(payment);
            }
        }
        return Optional.empty();
    }
}

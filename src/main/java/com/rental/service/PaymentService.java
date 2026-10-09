package com.rental.service;

import com.rental.model.Payment;
import com.rental.model.PaymentStatus;
import com.rental.model.Rental;
import com.rental.model.RentalStatus;
import com.rental.model.User;
import com.rental.model.Vehicle;
import com.rental.repository.PaymentRepository;
import com.rental.repository.RentalRepository;
import com.rental.repository.VehicleRepository;
import com.rental.util.IdGenerator;
import com.rental.util.ValidationUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Business rules for bills.
 *
 * <ul>
 *   <li>A bill is generated from a rental: base amount = days x the vehicle's
 *       polymorphic {@code calculateDailyRate()}.</li>
 *   <li>Each rental has at most one bill, and a CANCELLED rental cannot be billed.</li>
 *   <li>The total is calculated by the bill object itself ({@code calculateTotal()}),
 *       so cash and card bills follow their own fee rules.</li>
 *   <li>Customers can see only their own bills; admins see all.</li>
 * </ul>
 */
public class PaymentService {

    /** Prefix for payment ids (P001, P002 ...). */
    public static final String ID_PREFIX = "P";

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    /**
     * @param paymentRepository where bills are stored
     * @param rentalRepository  used to find the rental being billed
     * @param vehicleRepository used to get the vehicle's daily rate
     */
    public PaymentService(PaymentRepository paymentRepository, RentalRepository rentalRepository,
                          VehicleRepository vehicleRepository) {
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Creates the bill for a rental.
     *
     * @param method       "CASH" or "CARD"
     * @param lateDaysText late days from the form ("0" if returned on time)
     * @param extraValue   "received by" (cash) or last 4 card digits (card)
     * @return the saved bill (status PENDING)
     * @throws IllegalArgumentException if any rule is broken
     */
    public Payment generate(String rentalId, String method, String lateDaysText, String extraValue) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental " + rentalId + " was not found"));
        if (rental.getStatus() == RentalStatus.CANCELLED) {
            throw new IllegalArgumentException("Rental " + rental.getId() + " was cancelled and cannot be billed");
        }
        Optional<Payment> existing = paymentRepository.findByRentalId(rental.getId());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Rental " + rental.getId() + " already has bill " + existing.get().getId());
        }
        Vehicle vehicle = vehicleRepository.findById(rental.getVehicleId())
                .orElseThrow(() -> new IllegalArgumentException("Vehicle " + rental.getVehicleId() + " no longer exists"));

        double baseAmount = rental.getDays() * vehicle.calculateDailyRate(); // polymorphic rate
        String id = IdGenerator.next(ID_PREFIX, allIds());
        Payment payment = Payment.create(method, id, rental.getId(), rental.getCustomerId(), LocalDate.now(),
                baseAmount, ValidationUtil.parseInt(lateDaysText, "Late days"), PaymentStatus.PENDING, extraValue);
        paymentRepository.add(payment);
        return payment;
    }

    /**
     * Admin update: status, late days and the method-specific detail.
     *
     * @return the updated bill
     * @throws IllegalArgumentException if a value is invalid or the bill does not exist
     */
    public Payment update(String paymentId, String statusText, String lateDaysText, String extraValue) {
        Payment payment = getExisting(paymentId);
        payment.setStatus(parseStatus(statusText));
        payment.setLateDays(ValidationUtil.parseInt(lateDaysText, "Late days"));
        payment.setExtraValue(extraValue); // polymorphic: cash or card detail
        paymentRepository.update(payment);
        return payment;
    }

    /**
     * Deletes a bill (admin). The rental can then be billed again.
     *
     * @throws IllegalArgumentException if the bill does not exist
     */
    public void delete(String paymentId) {
        paymentRepository.delete(getExisting(paymentId).getId());
    }

    /**
     * Bills the user may see (admins: all, customers: their own), newest first.
     *
     * @param status "PENDING", "PAID", "OVERDUE" or blank for all
     */
    public List<Payment> findForUser(User user, String status) {
        List<Payment> result = new ArrayList<>();
        for (Payment payment : paymentRepository.findAll()) {
            boolean visible = user.canAccessAdminPages() || payment.getCustomerId().equalsIgnoreCase(user.getId());
            boolean statusMatches = status == null || status.isBlank() || payment.getStatus().name().equalsIgnoreCase(status);
            if (visible && statusMatches) {
                result.add(payment);
            }
        }
        result.sort(Comparator.comparing(Payment::getIssueDate).reversed());
        return result;
    }

    /**
     * @return the bill if it exists and the user may see it
     */
    public Optional<Payment> findVisible(String paymentId, User user) {
        return paymentRepository.findById(paymentId).filter(payment ->
                user.canAccessAdminPages() || payment.getCustomerId().equalsIgnoreCase(user.getId()));
    }

    /**
     * @return the bill with this id, if any (no ownership check)
     */
    public Optional<Payment> findById(String paymentId) {
        return paymentRepository.findById(paymentId);
    }

    /**
     * @return the bill of a rental, if one exists
     */
    public Optional<Payment> findByRentalId(String rentalId) {
        return paymentRepository.findByRentalId(rentalId);
    }

    /**
     * Rentals that can still be billed: not cancelled and without a bill.
     */
    public List<Rental> findBillableRentals() {
        List<Rental> result = new ArrayList<>();
        for (Rental rental : rentalRepository.findAll()) {
            if (rental.getStatus() != RentalStatus.CANCELLED
                    && paymentRepository.findByRentalId(rental.getId()).isEmpty()) {
                result.add(rental);
            }
        }
        return result;
    }

    /**
     * Sum of {@code calculateTotal()} for all bills with the given status.
     * <b>Polymorphism:</b> each bill adds its own fees.
     */
    public double totalByStatus(PaymentStatus status) {
        double total = 0;
        for (Payment payment : paymentRepository.findAll()) {
            if (payment.getStatus() == status) {
                total += payment.calculateTotal();
            }
        }
        return total;
    }

    // ----- helpers -----

    private PaymentStatus parseStatus(String text) {
        try {
            return PaymentStatus.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Status must be PENDING, PAID or OVERDUE");
        }
    }

    private Payment getExisting(String paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Bill " + paymentId + " was not found"));
    }

    private List<String> allIds() {
        List<String> ids = new ArrayList<>();
        for (Payment payment : paymentRepository.findAll()) {
            ids.add(payment.getId());
        }
        return ids;
    }
}

package com.rental.service;

import com.rental.model.Rental;
import com.rental.model.RentalStatus;
import com.rental.model.User;
import com.rental.model.Vehicle;
import com.rental.repository.RentalRepository;
import com.rental.repository.VehicleRepository;
import com.rental.util.IdGenerator;
import com.rental.util.ValidationUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Business rules for bookings.
 *
 * <ul>
 *   <li>The vehicle must exist and be in service (available).</li>
 *   <li>The start date cannot be in the past; the end date must be after the start date;
 *       a booking can be at most {@value #MAX_DAYS} days.</li>
 *   <li>No two ACTIVE bookings of the same vehicle may overlap.</li>
 *   <li>Only ACTIVE bookings can be changed, cancelled or returned.</li>
 *   <li>Customers may only change their own bookings; admins may change any booking.</li>
 * </ul>
 *
 * <p><b>OOP concept - Polymorphism:</b> "is this an admin?" is answered by
 * {@code user.canAccessAdminPages()}, never by {@code instanceof}.</p>
 */
public class RentalService {

    /** Prefix for rental ids (R001, R002 ...). */
    public static final String ID_PREFIX = "R";

    /** Longest booking allowed, in days. */
    public static final int MAX_DAYS = 30;

    private final RentalRepository rentalRepository;
    private final VehicleRepository vehicleRepository;

    /**
     * @param rentalRepository  where rentals are stored
     * @param vehicleRepository used to check that the vehicle exists and is available
     */
    public RentalService(RentalRepository rentalRepository, VehicleRepository vehicleRepository) {
        this.rentalRepository = rentalRepository;
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Creates a new ACTIVE booking for the logged-in user.
     *
     * @param customer  the user making the booking
     * @param vehicleId the vehicle to rent
     * @param startText start date from the form (yyyy-MM-dd)
     * @param endText   end date from the form (yyyy-MM-dd)
     * @return the saved rental
     * @throws IllegalArgumentException if any rule is broken
     */
    public Rental book(User customer, String vehicleId, String startText, String endText) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle " + vehicleId + " was not found"));
        if (!vehicle.isAvailable()) {
            throw new IllegalArgumentException(vehicle.getBrand() + " " + vehicle.getModel()
                    + " is not available for booking right now");
        }
        LocalDate start = ValidationUtil.parseDate(startText, "Start date");
        LocalDate end = ValidationUtil.parseDate(endText, "End date");
        checkDateRules(start, end, null);
        checkNoOverlap(vehicle.getId(), start, end, null);

        String id = IdGenerator.next(ID_PREFIX, allIds());
        Rental rental = new Rental(id, customer.getId(), vehicle.getId(), start, end, RentalStatus.ACTIVE);
        rentalRepository.add(rental);
        return rental;
    }

    /**
     * Changes the dates of an ACTIVE booking.
     *
     * @param user the logged-in user (must own the booking, or be an admin)
     * @return the updated rental
     * @throws IllegalArgumentException if any rule is broken
     */
    public Rental modifyDates(String rentalId, User user, String startText, String endText) {
        Rental rental = getManageable(rentalId, user);
        LocalDate start = ValidationUtil.parseDate(startText, "Start date");
        LocalDate end = ValidationUtil.parseDate(endText, "End date");
        checkDateRules(start, end, rental);
        checkNoOverlap(rental.getVehicleId(), start, end, rental.getId());
        rental.setDates(start, end);
        rentalRepository.update(rental);
        return rental;
    }

    /**
     * Cancels an ACTIVE booking.
     *
     * @param user the logged-in user (must own the booking, or be an admin)
     * @return the cancelled rental
     */
    public Rental cancel(String rentalId, User user) {
        Rental rental = getManageable(rentalId, user);
        rental.setStatus(RentalStatus.CANCELLED);
        rentalRepository.update(rental);
        return rental;
    }

    /**
     * Marks an ACTIVE booking as returned (admin action).
     *
     * @return the returned rental
     * @throws IllegalArgumentException if the rental is not active or has not started yet
     */
    public Rental markReturned(String rentalId) {
        Rental rental = getExisting(rentalId);
        if (!rental.isActive()) {
            throw new IllegalArgumentException("Only active rentals can be returned (" + rentalId
                    + " is " + rental.getStatus() + ")");
        }
        if (rental.getStartDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Rental " + rentalId + " has not started yet; cancel it instead");
        }
        rental.setStatus(RentalStatus.RETURNED);
        rentalRepository.update(rental);
        return rental;
    }

    /**
     * Estimated price of a rental: days x the vehicle's polymorphic daily rate.
     *
     * @return the cost in rupees, or 0 if the vehicle no longer exists
     */
    public double estimateCost(Rental rental) {
        return vehicleRepository.findById(rental.getVehicleId())
                .map(vehicle -> rental.getDays() * vehicle.calculateDailyRate())
                .orElse(0.0);
    }

    /**
     * Lists rentals the user is allowed to see: admins see all, customers only their own.
     * Newest start date first.
     *
     * @param status "ACTIVE", "RETURNED", "CANCELLED" or blank for all
     * @return the matching rentals
     */
    public List<Rental> findForUser(User user, String status) {
        List<Rental> source = user.canAccessAdminPages()
                ? rentalRepository.findAll()
                : rentalRepository.findByCustomerId(user.getId());
        List<Rental> result = new ArrayList<>();
        for (Rental rental : source) {
            if (status == null || status.isBlank() || rental.getStatus().name().equalsIgnoreCase(status)) {
                result.add(rental);
            }
        }
        result.sort(Comparator.comparing(Rental::getStartDate).reversed());
        return result;
    }

    /**
     * Returns a rental if the user may see it (owner or admin).
     *
     * @return the rental, or empty if it does not exist or belongs to someone else
     */
    public Optional<Rental> findVisible(String rentalId, User user) {
        return rentalRepository.findById(rentalId).filter(rental -> canManage(user, rental));
    }

    /**
     * @return the rental with this id, if any (no ownership check)
     */
    public Optional<Rental> findById(String rentalId) {
        return rentalRepository.findById(rentalId);
    }

    /**
     * ACTIVE bookings of a vehicle that end today or later, earliest first
     * (shown on the booking form so customers can see blocked dates).
     */
    public List<Rental> findUpcomingForVehicle(String vehicleId) {
        List<Rental> result = new ArrayList<>();
        for (Rental rental : rentalRepository.findByVehicleId(vehicleId)) {
            if (rental.isActive() && !rental.getEndDate().isBefore(LocalDate.now())) {
                result.add(rental);
            }
        }
        result.sort(Comparator.comparing(Rental::getStartDate));
        return result;
    }

    /**
     * @return true if the customer has a RETURNED rental of the vehicle (used for verified reviews)
     */
    public boolean hasReturnedRental(String customerId, String vehicleId) {
        for (Rental rental : rentalRepository.findByCustomerId(customerId)) {
            if (rental.getVehicleId().equalsIgnoreCase(vehicleId) && rental.getStatus() == RentalStatus.RETURNED) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return how many rentals have the given status
     */
    public long countByStatus(RentalStatus status) {
        return rentalRepository.findAll().stream().filter(r -> r.getStatus() == status).count();
    }

    /**
     * @return how many of this customer's rentals have the given status
     */
    public long countForCustomer(String customerId, RentalStatus status) {
        return rentalRepository.findByCustomerId(customerId).stream()
                .filter(r -> r.getStatus() == status).count();
    }

    // ----- rules and helpers -----

    /**
     * @param existing the booking being changed, or null for a new booking
     */
    private void checkDateRules(LocalDate start, LocalDate end, Rental existing) {
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End date must be after the start date");
        }
        boolean startUnchanged = existing != null && existing.getStartDate().equals(start);
        if (start.isBefore(LocalDate.now()) && !startUnchanged) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
        if (end.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("End date cannot be in the past");
        }
        if (ChronoUnit.DAYS.between(start, end) > MAX_DAYS) {
            throw new IllegalArgumentException("A booking can be at most " + MAX_DAYS + " days");
        }
    }

    /**
     * Throws if another ACTIVE booking of the same vehicle overlaps the dates.
     *
     * @param ignoreRentalId the booking being edited (it may overlap itself), or null
     */
    private void checkNoOverlap(String vehicleId, LocalDate start, LocalDate end, String ignoreRentalId) {
        for (Rental other : rentalRepository.findByVehicleId(vehicleId)) {
            if (other.getId().equalsIgnoreCase(ignoreRentalId)) {
                continue;
            }
            if (other.overlaps(start, end)) {
                throw new IllegalArgumentException("Vehicle is already booked from " + other.getStartDate()
                        + " to " + other.getEndDate() + " (" + other.getId() + "). Please choose other dates.");
            }
        }
    }

    /** The owner of a rental, or any admin, may manage it. */
    private boolean canManage(User user, Rental rental) {
        return user.canAccessAdminPages() || rental.getCustomerId().equalsIgnoreCase(user.getId());
    }

    /** Finds an ACTIVE rental that the user may change. */
    private Rental getManageable(String rentalId, User user) {
        Rental rental = getExisting(rentalId);
        if (!canManage(user, rental)) {
            throw new IllegalArgumentException("You can only change your own bookings");
        }
        if (!rental.isActive()) {
            throw new IllegalArgumentException("Rental " + rentalId + " is " + rental.getStatus()
                    + " and can no longer be changed");
        }
        return rental;
    }

    private Rental getExisting(String rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new IllegalArgumentException("Rental " + rentalId + " was not found"));
    }

    private List<String> allIds() {
        List<String> ids = new ArrayList<>();
        for (Rental rental : rentalRepository.findAll()) {
            ids.add(rental.getId());
        }
        return ids;
    }
}

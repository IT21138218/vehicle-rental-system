package com.rental.service;

import com.rental.model.Vehicle;
import com.rental.repository.VehicleRepository;
import com.rental.util.IdGenerator;
import com.rental.util.ValidationUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Business rules for the vehicle fleet: add, edit, delete, search and filter.
 *
 * <p>Form values arrive as text; this class converts and validates them, creates the
 * right subclass through {@link Vehicle#create} and saves it through the repository.</p>
 *
 * <p><b>OOP concept - Information hiding:</b> servlets never see vehicles.txt.</p>
 */
public class VehicleService {

    /** Prefix for vehicle ids (V001, V002 ...). */
    public static final String ID_PREFIX = "V";

    /** Filter value: only vehicles that can be booked. */
    public static final String AVAILABLE = "available";
    /** Filter value: only vehicles out of service. */
    public static final String UNAVAILABLE = "unavailable";

    private final VehicleRepository vehicleRepository;

    /**
     * @param vehicleRepository where vehicles are stored
     */
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Adds a new vehicle. The id is generated automatically.
     *
     * @param type      "CAR", "BIKE" or "VAN"
     * @param specText  seats / engine CC / cargo kg, as typed in the form
     * @return the saved vehicle
     * @throws IllegalArgumentException if any value is invalid
     */
    public Vehicle add(String type, String brand, String model, String yearText,
                       String rateText, boolean available, String specText) {
        String id = IdGenerator.next(ID_PREFIX, allIds());
        Vehicle vehicle = Vehicle.create(type, id, brand, model,
                ValidationUtil.parseInt(yearText, "Year"),
                ValidationUtil.parseDouble(rateText, "Daily rate"),
                available,
                ValidationUtil.parseInt(specText, "Type-specific value"));
        vehicleRepository.add(vehicle);
        return vehicle;
    }

    /**
     * Updates an existing vehicle. The type cannot change (a car stays a car).
     *
     * <p><b>Polymorphism:</b> {@code setSpecValue} updates seats, engine CC or cargo
     * depending on the real type of the object.</p>
     *
     * @return the updated vehicle
     * @throws IllegalArgumentException if the id does not exist or a value is invalid
     */
    public Vehicle update(String id, String brand, String model, String yearText,
                          String rateText, boolean available, String specText) {
        Vehicle vehicle = getExisting(id);
        vehicle.setBrand(brand);
        vehicle.setModel(model);
        vehicle.setYear(ValidationUtil.parseInt(yearText, "Year"));
        vehicle.setBaseDailyRate(ValidationUtil.parseDouble(rateText, "Daily rate"));
        vehicle.setAvailable(available);
        vehicle.setSpecValue(ValidationUtil.parseInt(specText, vehicle.getSpecLabel()));
        vehicleRepository.update(vehicle);
        return vehicle;
    }

    /**
     * Deletes a vehicle.
     *
     * @throws IllegalArgumentException if the vehicle does not exist
     */
    public void delete(String id) {
        Vehicle vehicle = getExisting(id);
        vehicleRepository.delete(vehicle.getId());
    }

    /**
     * @return the vehicle with this id, if any
     */
    public Optional<Vehicle> findById(String id) {
        return vehicleRepository.findById(id);
    }

    /**
     * @return every vehicle in file order
     */
    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    /**
     * @return map of vehicle id to vehicle, for showing names next to ids
     */
    public Map<String, Vehicle> findAllAsMap() {
        Map<String, Vehicle> map = new LinkedHashMap<>();
        for (Vehicle vehicle : vehicleRepository.findAll()) {
            map.put(vehicle.getId(), vehicle);
        }
        return map;
    }

    /**
     * Searches the fleet.
     *
     * @param keyword      part of the id, brand or model (blank = all)
     * @param type         "CAR", "BIKE", "VAN" or blank for all types
     * @param availability {@link #AVAILABLE}, {@link #UNAVAILABLE} or blank for both
     * @return matching vehicles
     */
    public List<Vehicle> search(String keyword, String type, String availability) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        List<Vehicle> result = new ArrayList<>();
        for (Vehicle vehicle : vehicleRepository.findAll()) {
            boolean keywordMatches = key.isEmpty()
                    || vehicle.getId().toLowerCase().contains(key)
                    || vehicle.getBrand().toLowerCase().contains(key)
                    || vehicle.getModel().toLowerCase().contains(key);
            boolean typeMatches = type == null || type.isBlank() || vehicle.getType().equalsIgnoreCase(type);
            boolean availabilityMatches = availability == null || availability.isBlank()
                    || (AVAILABLE.equals(availability) && vehicle.isAvailable())
                    || (UNAVAILABLE.equals(availability) && !vehicle.isAvailable());
            if (keywordMatches && typeMatches && availabilityMatches) {
                result.add(vehicle);
            }
        }
        return result;
    }

    /**
     * Counts vehicles of each type.
     *
     * @return map such as {CAR=5, BIKE=4, VAN=3}
     */
    public Map<String, Integer> countByType() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Vehicle vehicle : vehicleRepository.findAll()) {
            counts.merge(vehicle.getType(), 1, Integer::sum);
        }
        return counts;
    }

    /**
     * @return how many vehicles are in service
     */
    public long countAvailable() {
        return vehicleRepository.findAll().stream().filter(Vehicle::isAvailable).count();
    }

    // ----- helpers -----

    private Vehicle getExisting(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle " + id + " was not found"));
    }

    private List<String> allIds() {
        List<String> ids = new ArrayList<>();
        for (Vehicle vehicle : vehicleRepository.findAll()) {
            ids.add(vehicle.getId());
        }
        return ids;
    }
}

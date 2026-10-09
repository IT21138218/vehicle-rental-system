package com.rental.service;

import com.rental.model.Driver;
import com.rental.model.Staff;
import com.rental.repository.StaffRepository;
import com.rental.util.IdGenerator;
import com.rental.util.ValidationUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Business rules for drivers and mechanics: add, edit, remove and search.
 *
 * <p>Rule: phone numbers and driving licence numbers must be unique.</p>
 */
public class StaffService {

    /** Prefix for staff ids (S001, S002 ...). */
    public static final String ID_PREFIX = "S";

    private final StaffRepository staffRepository;

    /**
     * @param staffRepository where staff are stored
     */
    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    /**
     * Adds a driver or mechanic.
     *
     * @param type       "DRIVER" or "MECHANIC"
     * @param extraValue licence number or specialization
     * @return the saved staff member
     */
    public Staff add(String type, String name, String phone, String wageText, String extraValue) {
        String id = IdGenerator.next(ID_PREFIX, allIds());
        Staff staff = Staff.create(type, id, name, phone,
                ValidationUtil.parseDouble(wageText, "Daily wage"), extraValue);
        checkUnique(staff);
        staffRepository.add(staff);
        return staff;
    }

    /**
     * Updates a staff member. The type cannot change.
     *
     * <p><b>Polymorphism:</b> {@code setExtraValue} updates the licence or the
     * specialization depending on the real object.</p>
     *
     * @return the updated staff member
     */
    public Staff update(String id, String name, String phone, String wageText, String extraValue) {
        Staff staff = getExisting(id);
        staff.setName(name);
        staff.setPhone(phone);
        staff.setDailyWage(ValidationUtil.parseDouble(wageText, "Daily wage"));
        staff.setExtraValue(extraValue);
        checkUnique(staff);
        staffRepository.update(staff);
        return staff;
    }

    /**
     * Removes a staff member.
     *
     * @throws IllegalArgumentException if the id does not exist
     */
    public void delete(String id) {
        staffRepository.delete(getExisting(id).getId());
    }

    /**
     * @return the staff member with this id, if any
     */
    public Optional<Staff> findById(String id) {
        return staffRepository.findById(id);
    }

    /**
     * Searches staff.
     *
     * @param keyword part of the id, name, phone or licence/specialization (blank = all)
     * @param type    "DRIVER", "MECHANIC" or blank for both
     */
    public List<Staff> search(String keyword, String type) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        List<Staff> result = new ArrayList<>();
        for (Staff staff : staffRepository.findAll()) {
            boolean typeMatches = type == null || type.isBlank() || staff.getType().equalsIgnoreCase(type);
            boolean keywordMatches = key.isEmpty()
                    || staff.getId().toLowerCase().contains(key)
                    || staff.getName().toLowerCase().contains(key)
                    || staff.getPhone().contains(key)
                    || staff.getExtraValue().toLowerCase().contains(key);
            if (typeMatches && keywordMatches) {
                result.add(staff);
            }
        }
        return result;
    }

    /**
     * @return map such as {DRIVER=6, MECHANIC=4}
     */
    public Map<String, Integer> countByType() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        for (Staff staff : staffRepository.findAll()) {
            counts.merge(staff.getType(), 1, Integer::sum);
        }
        return counts;
    }

    // ----- helpers -----

    /** Throws if another staff member has the same phone, or the same licence number. */
    private void checkUnique(Staff candidate) {
        for (Staff other : staffRepository.findAll()) {
            if (other.getId().equalsIgnoreCase(candidate.getId())) {
                continue;
            }
            if (other.getPhone().equals(candidate.getPhone())) {
                throw new IllegalArgumentException("Phone " + candidate.getPhone() + " is already used by " + other.getName());
            }
            boolean bothDrivers = Driver.TYPE.equals(candidate.getType()) && Driver.TYPE.equals(other.getType());
            if (bothDrivers && other.getExtraValue().equalsIgnoreCase(candidate.getExtraValue())) {
                throw new IllegalArgumentException("Licence " + candidate.getExtraValue() + " is already registered to " + other.getName());
            }
        }
    }

    private Staff getExisting(String id) {
        return staffRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff member " + id + " was not found"));
    }

    private List<String> allIds() {
        List<String> ids = new ArrayList<>();
        for (Staff staff : staffRepository.findAll()) {
            ids.add(staff.getId());
        }
        return ids;
    }
}

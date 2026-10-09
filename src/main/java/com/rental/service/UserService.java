package com.rental.service;

import com.rental.model.Customer;
import com.rental.model.User;
import com.rental.repository.UserRepository;
import com.rental.util.IdGenerator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Business rules for user accounts: registration, search, profile updates and deletion.
 *
 * <p>Rules:</p>
 * <ul>
 *   <li>Usernames and e-mail addresses must be unique.</li>
 *   <li>New accounts created through registration are always customers.</li>
 *   <li>A user cannot delete their own account, and the last admin cannot be deleted.</li>
 * </ul>
 *
 * <p><b>OOP concept - Information hiding:</b> servlets call these methods and never touch
 * users.txt. All errors are reported as {@link IllegalArgumentException} with a friendly message.</p>
 */
public class UserService {

    /** Prefix for user ids (U001, U002 ...). */
    public static final String ID_PREFIX = "U";

    private final UserRepository userRepository;

    /**
     * @param userRepository where users are stored
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new customer account (self-registration).
     *
     * @return the saved customer with its new id
     * @throws IllegalArgumentException if a value is invalid or already used
     */
    public Customer register(String username, String password, String name, String email) {
        String id = IdGenerator.next(ID_PREFIX, allIds());
        Customer customer = new Customer(id, username, password, name, email);
        checkUnique(customer);
        userRepository.add(customer);
        return customer;
    }

    /**
     * @return every user in file order
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * @return the user with this id, if any
     */
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    /**
     * Returns users as a map from id to user, so pages can show a name for an id quickly.
     *
     * @return map of id to user, in file order
     */
    public Map<String, User> findAllAsMap() {
        Map<String, User> map = new LinkedHashMap<>();
        for (User user : userRepository.findAll()) {
            map.put(user.getId(), user);
        }
        return map;
    }

    /**
     * Searches users by keyword and role.
     *
     * @param keyword part of the id, username, name or e-mail (blank = everything)
     * @param role    "ADMIN", "CUSTOMER", or blank for all roles
     * @return the matching users
     */
    public List<User> search(String keyword, String role) {
        String key = keyword == null ? "" : keyword.trim().toLowerCase();
        List<User> result = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            boolean roleMatches = role == null || role.isBlank() || user.getRole().equalsIgnoreCase(role);
            boolean keywordMatches = key.isEmpty()
                    || user.getId().toLowerCase().contains(key)
                    || user.getUsername().toLowerCase().contains(key)
                    || user.getName().toLowerCase().contains(key)
                    || user.getEmail().contains(key);
            if (roleMatches && keywordMatches) {
                result.add(user);
            }
        }
        return result;
    }

    /**
     * Counts users that have a given role.
     *
     * @param role "ADMIN" or "CUSTOMER"
     * @return how many users have that role
     */
    public long countByRole(String role) {
        return userRepository.findAll().stream().filter(u -> u.getRole().equals(role)).count();
    }

    /**
     * Lets a logged-in user change their own name, e-mail and (optionally) password.
     *
     * @param currentPassword required only when a new password is given
     * @param newPassword     blank to keep the old password
     * @return the updated user
     */
    public User updateProfile(String userId, String name, String email,
                              String currentPassword, String newPassword) {
        User user = getExisting(userId);
        user.setName(name);
        user.setEmail(email);
        if (newPassword != null && !newPassword.isEmpty()) {
            if (!user.checkPassword(currentPassword)) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
            user.setPassword(newPassword);
        }
        checkUnique(user);
        userRepository.update(user);
        return user;
    }

    /**
     * Lets an admin edit any account. The admin may also set a new password.
     *
     * @param newPassword blank to keep the old password
     * @return the updated user
     */
    public User updateByAdmin(String userId, String username, String name, String email, String newPassword) {
        User user = getExisting(userId);
        user.setUsername(username);
        user.setName(name);
        user.setEmail(email);
        if (newPassword != null && !newPassword.isEmpty()) {
            user.setPassword(newPassword);
        }
        checkUnique(user);
        userRepository.update(user);
        return user;
    }

    /**
     * Deletes an account.
     *
     * @param userId          the account to delete
     * @param currentUserId   the admin performing the delete
     * @throws IllegalArgumentException if the delete breaks a rule
     */
    public void delete(String userId, String currentUserId) {
        User user = getExisting(userId);
        if (user.getId().equalsIgnoreCase(currentUserId)) {
            throw new IllegalArgumentException("You cannot delete your own account while logged in");
        }
        if (user.canAccessAdminPages() && countAdmins() <= 1) {
            throw new IllegalArgumentException("The last administrator account cannot be deleted");
        }
        userRepository.delete(user.getId());
    }

    // ----- helpers -----

    private User getExisting(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User " + userId + " was not found"));
    }

    private long countAdmins() {
        return userRepository.findAll().stream().filter(User::canAccessAdminPages).count();
    }

    /** Throws if another account already uses this username or e-mail. */
    private void checkUnique(User candidate) {
        for (User other : userRepository.findAll()) {
            if (other.getId().equalsIgnoreCase(candidate.getId())) {
                continue;
            }
            if (other.getUsername().equalsIgnoreCase(candidate.getUsername())) {
                throw new IllegalArgumentException("Username '" + candidate.getUsername() + "' is already taken");
            }
            if (other.getEmail().equalsIgnoreCase(candidate.getEmail())) {
                throw new IllegalArgumentException("Email " + candidate.getEmail() + " is already registered");
            }
        }
    }

    private List<String> allIds() {
        List<String> ids = new ArrayList<>();
        for (User user : userRepository.findAll()) {
            ids.add(user.getId());
        }
        return ids;
    }
}

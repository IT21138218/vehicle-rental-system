package com.rental.service;

import com.rental.model.User;
import com.rental.repository.UserRepository;

import java.util.Optional;

/**
 * Checks login details.
 *
 * <p><b>OOP concept - Information hiding:</b> the LoginServlet asks "is this username and
 * password valid?" and gets back a User; it never reads users.txt itself.</p>
 */
public class AuthService {

    private final UserRepository userRepository;

    /**
     * @param userRepository where users are stored
     */
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns the user when the username and password match.
     *
     * @param username login name typed by the user
     * @param password password typed by the user
     * @return the logged-in user (an AdminUser or Customer), or empty if the details are wrong
     */
    public Optional<User> login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username.trim())
                .filter(user -> user.checkPassword(password));
    }
}

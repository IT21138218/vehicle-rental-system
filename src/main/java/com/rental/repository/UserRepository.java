package com.rental.repository;

import com.rental.model.User;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Stores users in users.txt.
 *
 * <p>Line format: {@code id,role,username,password,name,email}<br>
 * Example: {@code U002,CUSTOMER,nimal,pass123,Nimal Perera,nimal@gmail.com}</p>
 *
 * <p><b>OOP concepts - Inheritance + Information hiding:</b> all CRUD code is inherited
 * from {@link AbstractFileRepository}; this class only knows how to read one user line.</p>
 */
public class UserRepository extends AbstractFileRepository<User> {

    /** Name of the data file. */
    public static final String FILE_NAME = "users.txt";

    private static final int FIELD_COUNT = 6;

    /**
     * @param filePath full path of users.txt
     */
    public UserRepository(Path filePath) {
        super(filePath);
    }

    /**
     * Factory step: the role in field 1 decides whether an AdminUser or a Customer is built.
     *
     * @param fields {id, role, username, password, name, email}
     */
    @Override
    protected User parse(String[] fields) {
        if (fields.length != FIELD_COUNT) {
            throw new IllegalArgumentException("expected " + FIELD_COUNT + " fields but found " + fields.length);
        }
        return User.create(fields[1], fields[0], fields[2], fields[3], fields[4], fields[5]);
    }

    /**
     * Finds a user by login name (not case-sensitive).
     *
     * @param username the login name
     * @return the user, or empty if nobody has that username
     */
    public Optional<User> findByUsername(String username) {
        for (User user : findAll()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}

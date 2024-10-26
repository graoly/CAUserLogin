package data_access;

import java.util.HashMap;
import java.util.Map;

import entity.User;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

/**
 * In-memory implementation of the DAO for storing user data. This
 * implementation does NOT persist data between runs of the program.
 */
public class InMemoryUserDataAccessObject implements
        SignupUserDataAccessInterface, LoginUserDataAccessInterface,
        ChangePasswordUserDataAccessInterface {

    /**
     * Map of Strings to Users.
     */
    private final Map<String, User> users = new HashMap<>();

    /**
     * Current user.
     */
    private String currentUser;

    /**
     * Override getter method for current user.
     * @return current User
     */
    @Override
    public String getCurrentUser() {
        return currentUser;
    }

    /**
     * Override the exists by name method.
     * @param identifier the username to look for
     * @return if the user contains the key.
     */
    @Override
    public boolean existsByName(final String identifier) {
        return users.containsKey(identifier);
    }

    /**
     * Override save method.
     * @param user the user to save
     */
    @Override
    public void save(final User user) {
        users.put(user.getName(), user);
    }

    /**
     * Override get method.
     * @param username the username to look up
     * @return username of User.
     */
    @Override
    public User get(final String username) {
        return users.get(username);
    }

    /**
     * Override setter method for user.
     * @param name of the user
     */
    @Override
    public void setCurrentUser(final String name) {
        this.currentUser = name;
    }

    /**
     * Override change password method.
     * @param user the user whose password is to be updated
     */
    @Override
    public void changePassword(final User user) {
        // Replace the old entry with the new password
        users.put(user.getName(), user);
    }

}

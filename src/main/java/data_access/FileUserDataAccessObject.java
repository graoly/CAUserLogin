package data_access;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import entity.User;
import entity.UserFactory;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

/**
 * DAO for user data implemented using a File to persist the data.
 */
public class FileUserDataAccessObject implements
        SignupUserDataAccessInterface, LoginUserDataAccessInterface,
        ChangePasswordUserDataAccessInterface {

    /**
     * Header = "username,password".
     */
    private static final String HEADER = "username,password";

    /**
     * File csvFile.
     */
    private final File csvFile;
    /**
     * Map from String to Integer.
     */
    private final Map<String, Integer> headers = new LinkedHashMap<>();
    /**
     * Map from String to User.
     */
    private final Map<String, User> accounts = new HashMap<>();

    /**
     * FileUserDataAccessObject.
     * @param csvPath a String
     * @param userFactory a userFactory
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public FileUserDataAccessObject(final String csvPath,
                                    final UserFactory userFactory)
            throws IOException {

        csvFile = new File(csvPath);
        headers.put("username", 0);
        headers.put("password", 1);

        if (csvFile.length() == 0) {
            save();
        }
        else {

            try (BufferedReader reader = new BufferedReader(
                    new FileReader(csvFile))) {
                final String header = reader.readLine();

                if (!header.equals(HEADER)) {
                    throw new RuntimeException(String.format(
                            "header should be%n: %s%but was:%n%s",
                            HEADER, header));
                }

                String row;
                while ((row = reader.readLine()) != null) {
                    final String[] col = row.split(",");
                    final String username = String.valueOf(col[headers
                            .get("username")]);
                    final String password = String.valueOf(col[headers
                            .get("password")]);
                    final User user = userFactory.create(username, password);
                    accounts.put(username, user);
                }
            }
        }
    }

    private void save() {
        final BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(csvFile));
            writer.write(String.join(",", headers.keySet()));
            writer.newLine();

            for (User user : accounts.values()) {
                final String line = String.format("%s,%s",
                        user.getName(), user.getPassword());
                writer.write(line);
                writer.newLine();
            }

            writer.close();

        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Overrides save method.
     * @param user the user to save
     */
    @Override
    public void save(final User user) {
        accounts.put(user.getName(), user);
        this.save();
    }

    /**
     * Overrides get method.
     * @param username the username to look up
     * @return a User with the given username
     */
    @Override
    public User get(final String username) {
        return accounts.get(username);
    }

    /**
     * Overrides setter for current user.
     * @param name of the user
     */
    @Override
    public void setCurrentUser(final String name) {

    }

    /**
     * Overrides getter for current user.
     * @return the empty string
     */
    @Override
    public String getCurrentUser() {
        return "";
    }

    /**
     * Overrides exists by name method.
     * @param identifier the username to look for
     * @return if accounts contains key
     */
    @Override
    public boolean existsByName(final String identifier) {
        return accounts.containsKey(identifier);
    }

    /**
     * Override change password method.
     * @param user the user whose password is to be updated
     */
    @Override
    public void changePassword(final User user) {
        // Replace the User object in the map
        accounts.put(user.getName(), user);
        save();
    }
}

package data_access;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import entity.User;
import entity.UserFactory;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import use_case.change_password.ChangePasswordUserDataAccessInterface;
import use_case.login.LoginUserDataAccessInterface;
import use_case.signup.SignupUserDataAccessInterface;

/**
 * The DAO for user data.
 */
public class DBUserDataAccessObject implements
        SignupUserDataAccessInterface, LoginUserDataAccessInterface,
        ChangePasswordUserDataAccessInterface {
    /**
     * SUCCESS_CODE = 200.
     */
    private static final int SUCCESS_CODE = 200;
    /**
     * CONTENT_TYPE_LABEL = "Content-Type".
     */
    private static final String CONTENT_TYPE_LABEL = "Content-Type";
    /**
     * CONTENT_TYPE_JSON = "application/json".
     */
    private static final String CONTENT_TYPE_JSON = "application/json";
    /**
     * STATUS_CODE_LABEL = "status_code".
     */
    private static final String STATUS_CODE_LABEL = "status_code";
    /**
     * USERNAME = "username".
     */
    private static final String USERNAME = "username";
    /**
     * PASSWORD = "password".
     */
    private static final String PASSWORD = "password";
    /**
     * MESSAGE = "message".
     */
    private static final String MESSAGE = "message";
    /**
     * UserFactory.
     */
    private final UserFactory userFactory;

    /**
     * DBUserDataAccessObject method.
     * @param userobjFactory the user factory
     */
    public DBUserDataAccessObject(final UserFactory userobjFactory) {
        this.userFactory = userobjFactory;
        // No need to do anything to reinitialize a user list! The data is the
        // cloud that may be miles away.
    }

    /**
     * Override get method.
     * @param username the username to look up
     * @return User
     */
    @Override
    public User get(final String username) {
        // Make an API call to get the user object.
        final OkHttpClient client = new OkHttpClient().newBuilder().build();
        final Request request = new Request.Builder()
                .url(String.format("http://vm003.teach.cs."
                        + "toronto.edu:20112/user?username=%s", username))
                .addHeader("Content-Type", CONTENT_TYPE_JSON)
                .build();
        try {
            final Response response = client.newCall(request).execute();

            final JSONObject responseBody = new JSONObject(response.body()
                    .string());

            if (responseBody.getInt(STATUS_CODE_LABEL) == SUCCESS_CODE) {
                final JSONObject userJSONObject = responseBody.getJSONObject(
                        "user");
                final String name = userJSONObject.getString(USERNAME);
                final String password = userJSONObject.getString(PASSWORD);

                return userFactory.create(name, password);
            }
            else {
                throw new RuntimeException(responseBody.getString(MESSAGE));
            }
        }
        catch (IOException | JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void setCurrentUser(final String name) {

    }

    /**
     * Overrid User getter method.
     * @return empty string
     */
    @Override
    public String getCurrentUser() {
        return "";
    }

    /**
     * Override exists by name method.
     * @param username the username to look for
     * @return true or false
     */
    @Override
    public boolean existsByName(final String username) {
        final OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        final Request request = new Request.Builder()
                .url(String.format("http://vm003.teach.cs.toronto.edu:20112"
                        + "/checkIfUserExists?username=%s", username))
                .addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_JSON)
                .build();
        try {
            final Response response = client.newCall(request).execute();

            final JSONObject responseBody = new JSONObject(
                    response.body().string());

            // throw new RuntimeException(responseBody.getString("message"));
            return responseBody.getInt(STATUS_CODE_LABEL) == SUCCESS_CODE;
        }
        catch (IOException | JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Override save method.
     * @param user the user to save
     */
    @Override
    public void save(final User user) {
        final OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        // POST METHOD
        final MediaType mediaType = MediaType.parse(CONTENT_TYPE_JSON);
        final JSONObject requestBody = new JSONObject();
        requestBody.put(USERNAME, user.getName());
        requestBody.put(PASSWORD, user.getPassword());
        final RequestBody body = RequestBody.create(requestBody.toString(),
                mediaType);
        final Request request = new Request.Builder()
                .url("http://vm003.teach.cs.toronto.edu:20112/user")
                .method("POST", body)
                .addHeader(CONTENT_TYPE_LABEL, CONTENT_TYPE_JSON)
                .build();
        try {
            final Response response = client.newCall(request).execute();

            final JSONObject responseBody = new JSONObject(response.body()
                    .string());

            if (responseBody.getInt(STATUS_CODE_LABEL) == SUCCESS_CODE) {
                // success!
            }
            else {
                throw new RuntimeException(responseBody.getString(MESSAGE));
            }
        }
        catch (IOException | JSONException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Override change password method.
     * @param user the user whose password is to be updated
     */
    @Override
    public void changePassword(final User user) {
        final OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();

        // POST METHOD
        final MediaType mediaType = MediaType.parse(CONTENT_TYPE_JSON);
        final JSONObject requestBody = new JSONObject();
        requestBody.put(USERNAME, user.getName());
        requestBody.put(PASSWORD, user.getPassword());
        final RequestBody body = RequestBody.create(requestBody.toString(),
                mediaType);
        final Request request = new Request.Builder()
                                    .url("http://vm003.teach.cs.toronto.edu"
                                            + ":20112/user")
                                    .method("PUT", body)
                                    .addHeader(CONTENT_TYPE_LABEL,
                                            CONTENT_TYPE_JSON)
                                    .build();
        try {
            final Response response = client.newCall(request).execute();

            final JSONObject responseBody = new JSONObject(response.body()
                    .string());

            if (responseBody.getInt(STATUS_CODE_LABEL) == SUCCESS_CODE) {
                // success!
            }
            else {
                throw new RuntimeException(responseBody.getString(MESSAGE));
            }
        }
        catch (IOException | JSONException ex) {
            throw new RuntimeException(ex);
        }
    }
}

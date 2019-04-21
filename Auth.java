import java.security.MessageDigest;
import java.io.IOException;

/**
 * Handles authentication for a user.
 *
 * @author Steven Wade
 */
public class Auth {
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private User user;

    private int loginAttempts = 0;

    private AuthRepository repository;

    /**
     * Takes a repository instance that handles actually
     * fetching the user record from a data store.
     *
     * @param repository
     */
    public Auth(AuthRepository repository) {
        this.repository = repository;
    }

    /**
     * Looks up a user record in the data store. If one is found, it
     * will set an instance of a User on this class. Three failed
     * login attempts will trigger an error.
     *
     * @param username the username of a user to lookup.
     * @param password the raw text password for a user to hash and lookup.
     * @throws Exception
     */
    public void login(String username, String password) throws Exception {
        String[] record = repository.findUserRecord(username, hash(password));

        // If the repository found a matching user, we'll have a populated array here.
        if (record.length > 0) {
            user = new User(record[0], record[1], record[2]);
        }

        // Increment the login attempts prior to checking. That way we can see earlier
        // if the user has tried to many times and failed.
        if (++loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            throw new Exception("Too many failed login attempts");
        }
    }

    /**
     * Logs the user out by resetting the login attempts
     * and unsetting the user record.
     */
    public void logout() {
        loginAttempts = 0;
        user = null;
    }

    /**
     * @return whether a user is logged in or not.
     */
    public boolean isLoggedIn() {
        return user instanceof User;
    }

    /**
     * Getter for the current logged in user. Can be
     * null if no one is currently logged in.
     *
     * @return the instance of the logged in User.
     */
    public User getUser() {
        return user;
    }

    /**
     * Hashes a raw text string.
     *
     * @param password the raw text password to hash.
     * @return the md5 hash of the password.
     * @throws Exception
     */
    public String hash(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        byte[] digest = md.digest();
        StringBuffer sb = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }

        return sb.toString();
    }
}
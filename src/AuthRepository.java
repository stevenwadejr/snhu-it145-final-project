/**
 * Interface for finding a user record for an
 * implemented data store.
 *
 * @author Steven Wade
 */
public interface AuthRepository {

    public String[] findUserRecord(String username, String passwordHash) throws Exception;
}
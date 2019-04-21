import java.util.Scanner;
import java.io.FileInputStream;

/**
 * Implementation for the AuthRepository that looks up
 * user records in a credentials text file.
 *
 * @author Steven Wade
 */
public class CredentialsFileRepository implements AuthRepository {

    private static final String CREDENTIALS_FILE = "credentials.txt";

    /**
     * Find the record of a user matching a given username
     * and password (hash) in a credentials text file.
     *
     * @param username     the username of the user to look up.
     * @param passwordHash hashed password of the user record.
     * @return a raw user record array consisting of username, passwordHash, and role.
     * @throws Exception
     */
    public String[] findUserRecord(String username, String passwordHash) throws Exception {
        // Use a try with resources here to open a stream to a file.
        // This will auto-close the file when the block is finished executing.
        // see: https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
        try (FileInputStream stream = new FileInputStream(CREDENTIALS_FILE)) {
            Scanner scnr = new Scanner(stream);

            // Each line in the file represents a user record. Examine each
            // one to determine a match.
            while (scnr.hasNextLine()) {
                // User record fields are separated by a tab character. Split on tabs
                // and populate a row with individual fields.
                String[] row = scnr.nextLine().split("\\t");

                // Check to see if a row matches the given credentials. If we find a match
                // earlier, return the record as there's no reason to continue parsing
                // lines from the credentials file.
                if (row[0].equals(username) && row[1].equals(passwordHash)) {
                    String[] record = new String[]{row[0], row[1], row[3]};
                    return record;
                }
            }
        }

        // No record was found, so return an empty array.
        return new String[0];
    }
}
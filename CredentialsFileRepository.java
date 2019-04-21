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
     * @param username the username of the user to look up.
     * @param passwordHash hashed password of the user record.
     * @return a raw user record array consisting of username, passwordHash, and role.
     * @throws Exception
     */
    public String[] findUserRecord(String username, String passwordHash) throws Exception {
        try (FileInputStream stream = new FileInputStream(CREDENTIALS_FILE)) {
            Scanner scnr = new Scanner(stream);

            while (scnr.hasNextLine()) {
                String[] row = scnr.nextLine().split("\\t");

                if (row[0].equals(username) && row[1].equals(passwordHash)) {
                    String[] record = new String[]{row[0], row[1], row[3]};
                    return record;
                }
            }
        }


        return new String[0];
    }
}
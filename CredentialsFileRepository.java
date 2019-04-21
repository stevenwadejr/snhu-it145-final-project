import java.util.Scanner;
import java.io.FileInputStream;

public class CredentialsFileRepository implements AuthRepository {

    private static final String CREDENTIALS_FILE = "credentials.txt";

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
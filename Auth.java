import java.security.MessageDigest;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.IOException;

public class Auth {
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private static final String CREDENTIALS_FILE = "credentials.txt";

    private User user;

    private int loginAttempts = 0;

    public void login(String username, String password) throws Exception {
        FileInputStream stream = new FileInputStream(CREDENTIALS_FILE);
        Scanner scnr = new Scanner(stream);
        boolean userFound = false;
        String passwordHash = hash(password);

        while (scnr.hasNextLine()) {
            String[] row = scnr.nextLine().split("\\t");

            if (row[0].equals(username) && row[1].equals(passwordHash)) {

                user = new User(row[0], row[1], row[3]);
                stream.close();
                return;
            }
        }

        stream.close();

        if (++loginAttempts >= MAX_LOGIN_ATTEMPTS) {
            throw new Exception("Too many failed login attempts");
        }
    }

    public void logout() {
        loginAttempts = 0;
        user = null;
    }

    public boolean isLoggedIn() {
        return user instanceof User;
    }

    public User getUser() {
        return user;
    }

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
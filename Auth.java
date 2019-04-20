import java.security.MessageDigest;
import java.util.Scanner;
import java.io.FileInputStream;
import java.io.IOException;

public class Auth {
    private static String credentialsPath = "credentials.txt";

    private User user;

    private int loginAttempts = 0;

    private int maxLoginAttempts = 3;

    public void login(String username, String password) throws Exception {
        FileInputStream stream = new FileInputStream(Auth.credentialsPath);
        Scanner scnr = new Scanner(stream);
        boolean userFound = false;
        String passwordHash = this.hash(password);

        while (scnr.hasNextLine()) {
            String[] row = scnr.nextLine().split("\\t");

            if (row[0].equals(username) && row[1].equals(passwordHash)) {

                this.user = new User(row[0], row[1], row[3]);
                stream.close();
                return;
            }
        }

        stream.close();

        if (++this.loginAttempts >= this.maxLoginAttempts) {
            throw new Exception("Too many failed login attempts");
        }
    }

    public void logout() {
        this.loginAttempts = 0;
        this.user = null;
    }

    public boolean isLoggedIn() {
        return this.user instanceof User;
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
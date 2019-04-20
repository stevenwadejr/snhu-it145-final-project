import java.util.Scanner;
import java.io.Console;
import java.io.FileInputStream;
import java.io.File;

public class AuthenticationSystem {
    private static final char OPTION_LOGIN = '1';

    private static final char OPTION_LOGOUT = '2';

    private static final char OPTION_QUIT = 'q';

    private static Auth auth;

    public static void main(String[] args) {
        Console cons = System.console();
        boolean shouldQuit = false;
        AuthenticationSystem.auth = new Auth();

        while (shouldQuit == false) {
            char selectedOption = showMenu();

            switch (selectedOption) {
                case AuthenticationSystem.OPTION_LOGIN:
                    clearScreen();

                    try {
                        while (auth.isLoggedIn() == false) {
                            String username = cons.readLine("Username: ");
                            String password = new String(cons.readPassword("Password: "));

                            auth.login(username, password);

                            if (auth.isLoggedIn() == false) {
                                System.out.println("Incorrect credentials, please try again");
                            }
                        }

                        clearScreen();
                        System.out.println("");
                        showRoleInfo(auth.getUser());
                    } catch (Exception e) {
                        System.out.println("Error logging in: \"" + e.getMessage() + "\"");
                        shouldQuit = true;
                    }
                    break;
                case AuthenticationSystem.OPTION_LOGOUT:
                    clearScreen();
                    auth.logout();
                    break;
                case AuthenticationSystem.OPTION_QUIT:
                    shouldQuit = true;
                    break;
            }
        }
    }

    private static char showMenu() {
        Scanner scnr = new Scanner(System.in);
        boolean validOption = false;
        char selectedOption = AuthenticationSystem.OPTION_QUIT;

        while (validOption == false) {
            System.out.println("");

            if (AuthenticationSystem.auth.isLoggedIn() == false) {
                System.out.println(AuthenticationSystem.OPTION_LOGIN + ": Login");
            } else {
                System.out.println(AuthenticationSystem.OPTION_LOGOUT + ": Logout");
            }
            System.out.println(AuthenticationSystem.OPTION_QUIT + ": Quit");
            System.out.println("");
            System.out.print("Choose an option: ");

            selectedOption = scnr.next().charAt(0);

            switch (selectedOption) {
                case AuthenticationSystem.OPTION_LOGIN:
                case AuthenticationSystem.OPTION_LOGOUT:
                case AuthenticationSystem.OPTION_QUIT:
                    validOption = true;
                    break;
                default:
                    System.out.println(
                            String.format(
                                    "\nOption \"%s\" was not a valid selection.",
                                    selectedOption
                            )
                    );
                    break;
            }
        }

        return selectedOption;
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void showRoleInfo(User user) throws Exception {
        File roleFile = new File("roles/" + user.getRole() + ".txt");
        if (roleFile.isFile() && roleFile.canRead()) {
            FileInputStream stream = new FileInputStream(roleFile);
            Scanner scnr = new Scanner(stream);

            while (scnr.hasNextLine()) {
                System.out.println(scnr.nextLine());
            }

            System.out.println("");
        } else {
            throw new Exception("Cannot read role file: " + user.getRole());
        }
    }
}
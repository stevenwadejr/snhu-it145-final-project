import static java.lang.System.out;

import java.util.Scanner;
import java.io.Console;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.HashMap;

/**
 * Authentication System
 * <p>
 * This program allows a Zoo employee to log in and will display
 * the appropriate information file based on the role of the user.
 *
 * @author Steven Wade
 */
public class AuthenticationSystem {

    private static enum MenuOption {
        LOGIN("Login", '1'),
        LOGOUT("Logout", '2'),
        QUIT("Quit", 'q');

        public final String label;
        public final char selector;

        private static final Map<Character, MenuOption> BY_SELECTOR = new HashMap<>();

        static {
            for (MenuOption o : values()) {
                BY_SELECTOR.put(o.selector, o);
            }
        }

        private MenuOption(String label, char selector) {
            this.label = label;
            this.selector = selector;
        }

        public String option() {
            return selector + ": " + label;
        }

        public static MenuOption[] authenticatedOptions() {
            MenuOption[] options = {LOGOUT, QUIT};
            return options;
        }

        public static MenuOption[] unauthenticatedOptions() {
            MenuOption[] options = {LOGIN, QUIT};
            return options;
        }

        public static MenuOption valueOfSelector(char selector) {
            return BY_SELECTOR.get(selector);
        }
    }

    private static Auth auth;

    public static void main(String[] args) {
        boolean shouldQuit = false;
        auth = new Auth(new CredentialsFileRepository());

        while (shouldQuit == false) {
            MenuOption selectedOption = getSelectedMenuOption();

            switch (selectedOption) {
                case LOGIN:
                    try {
                        clearScreen();
                        doLogin();
                    } catch (Exception e) {
                        out.println("Error logging in: \"" + e.getMessage() + "\"");
                        shouldQuit = true;
                    }
                    break;
                case LOGOUT:
                    clearScreen();
                    auth.logout();
                    break;
                case QUIT:
                    shouldQuit = true;
                    break;
            }
        }
    }

    /**
     * Prompts the user to select an option from menu. Will continue
     * to prompt the user until a valid selection is made.
     *
     * @return a selected CLI menu option
     */
    private static MenuOption getSelectedMenuOption() {
        Scanner scnr = new Scanner(System.in);
        MenuOption selectedOption = null;

        while (selectedOption == null) {
            char selection;

            showMenu();

            selection = scnr.next().charAt(0);
            selectedOption = MenuOption.valueOfSelector(selection);

            if (selectedOption == null) {
                out.println(
                        String.format(
                                "\nOption \"%s\" was not a valid selection.",
                                selection
                        )
                );
            }
        }

        return selectedOption;
    }

    /**
     * Outputs a list of menu items to the screen with a
     * "selector: Label" format. The options display differ
     * based on whether the user is logged in already or not.
     */
    private static void showMenu() {
        MenuOption[] options = auth.isLoggedIn()
                ? MenuOption.authenticatedOptions()
                : MenuOption.unauthenticatedOptions();

        out.println("");

        for (MenuOption o : options) {
            out.println(o.option());
        }

        out.print("Choose an option: ");
    }

    /**
     * Clears the screen of all input.
     */
    private static void clearScreen() {
        out.print("\033[H\033[2J");
        out.flush();
    }

    /**
     * Shows the role information for a user based on their role.
     *
     * @param user a user to display the appropriate role information for.
     * @throws Exception
     */
    private static void showRoleInfo(User user) throws Exception {
        String roleFile = "roles/" + user.getRole() + ".txt";

        try (FileInputStream stream = new FileInputStream(roleFile)) {
            Scanner scnr = new Scanner(stream);

            while (scnr.hasNextLine()) {
                out.println(scnr.nextLine());
            }

            out.println("");
        } catch (FileNotFoundException e) {
            throw new Exception("Cannot read role file: " + user.getRole());
        }
    }

    /**
     * Attempts to log in the user, giving them a few chances to authenticate.
     * Upon authentication, information specific to the role of the
     * authenticated user will be displayed.
     *
     * @throws Exception
     */
    private static void doLogin() throws Exception {
        Console cons = System.console();

        while (auth.isLoggedIn() == false) {
            String username = cons.readLine("Username: ");
            String password = new String(cons.readPassword("Password: "));

            auth.login(username, password);

            if (auth.isLoggedIn() == false) {
                out.println("Incorrect credentials, please try again");
            }
        }

        clearScreen();
        out.println("");
        showRoleInfo(auth.getUser());
    }
}
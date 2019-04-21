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
            // Populate the BY_SELECTOR hashmap with all MenuOptions
            // and keyed by their selector.
            for (MenuOption o : values()) {
                BY_SELECTOR.put(o.selector, o);
            }
        }

        private MenuOption(String label, char selector) {
            this.label = label;
            this.selector = selector;
        }

        /**
         * Helper function for formatting the menu option in a
         * user friendly format with the selector and label.
         *
         * @return user friendly string representation of the selector and label.
         */
        public String option() {
            return selector + ": " + label;
        }

        /**
         * Get a list of menu options to show when a user is authenticated.
         * (You wouldn't show "login" when they're already logged in).
         *
         * @return array of menu options.
         */
        public static MenuOption[] authenticatedOptions() {
            MenuOption[] options = {LOGOUT, QUIT};
            return options;
        }

        /**
         * Get a list of menu options to show when no one is authenticated.
         * (You wouldn't show "logout" when they're already logged out).
         *
         * @return array of menu options.
         */
        public static MenuOption[] unauthenticatedOptions() {
            MenuOption[] options = {LOGIN, QUIT};
            return options;
        }

        /**
         * Helper function to find the menu option based on its selector.
         *
         * @param selector the character of a menu item to look it up by.
         * @return a MenuOption if one matching the given selector was found.
         */
        public static MenuOption valueOfSelector(char selector) {
            return BY_SELECTOR.get(selector);
        }
    }

    private static Auth auth;

    public static void main(String[] args) {
        // Start with a clean slate for that "new program" look.
        clearScreen();

        // Instantiate the Auth class with a repository to look up
        // user records from a credentials file.
        auth = new Auth(new CredentialsFileRepository());

        boolean shouldQuit = false;

        while (shouldQuit == false) {
            MenuOption selectedOption = getSelectedMenuOption();

            switch (selectedOption) {
                case LOGIN:
                    try {
                        clearScreen();
                        doLogin();
                    } catch (Exception e) {
                        // If we hit an error here, let's bail and quit the program.
                        // Either something went wrong with finding a role file, or
                        // we took the "user not found" shortcut. Either way, bail.
                        out.println("Error logging in: \"" + e.getMessage() + "\"");
                        shouldQuit = true;
                    }
                    break;
                case LOGOUT:
                    // Always clear the screen on logout as you don't want to leave role
                    // specific information up on the screen.
                    clearScreen();

                    auth.logout();
                    break;
                case QUIT:
                    // Always clear the screen on exit as you don't want to leave role
                    // specific information up on the screen.
                    clearScreen();

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

            // Show the menu to the user until they choose a valid option.
            showMenu();

            // We're matching a MenuItem on a character here, so be sure to grab
            // the first character entered and set its type.
            selection = scnr.next().charAt(0);

            // Try to match a MenuItem by its selector. If none found, it'll return null.
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
        // Determine which menu options should be shown based
        // on whether the user is logged in already or not.
        MenuOption[] options = auth.isLoggedIn()
                ? MenuOption.authenticatedOptions()
                : MenuOption.unauthenticatedOptions();

        // Loop through each menu option and call its "option()"
        // helper method to display a user friendly prompt.
        for (MenuOption o : options) {
            out.println(o.option());
        }

        // Now prompt the user to select one of the options listed above.
        out.print("Choose an option: ");
    }

    /**
     * Clears the screen of all input.
     *
     * @see https://stackoverflow.com/a/32295974
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

        // Use a try with resources here to open a stream to a file.
        // This will auto-close the file when the block is finished executing.
        // see: https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
        try (FileInputStream stream = new FileInputStream(roleFile)) {
            Scanner scnr = new Scanner(stream);

            while (scnr.hasNextLine()) {
                out.println(scnr.nextLine());
            }

            out.println("");
        } catch (FileNotFoundException e) {
            // This is the only specific exception we want to catch here. The reason
            // being is so we can throw a new exception up the chain, with a more
            // specific/user friendly error message.
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
        // Use a Console here instead of Scanner so we can read and hide the passsword field.
        Console cons = System.console();

        // Keep letting the user try to log in. The Auth class will handle
        // how many times they can try and fail (hence the "throws" above).
        while (auth.isLoggedIn() == false) {
            String username = cons.readLine("Username: ");
            String password = new String(cons.readPassword("Password: "));

            auth.login(username, password);

            if (auth.isLoggedIn() == false) {
                out.println("Incorrect credentials, please try again");
            }
        }

        clearScreen();

        showRoleInfo(auth.getUser());
    }
}
import java.util.Scanner;
import java.io.Console;
import java.io.FileInputStream;
import java.io.File;
import java.util.Map;
import java.util.HashMap;

public class AuthenticationSystem {

    private static enum MenuOption {
        LOGIN("Login", '1'),
        LOGOUT("Logout", '2'),
        QUIT("Quit", 'q');

        public final String label;
        public final char selector;

        private static final Map<Character, MenuOption> BY_SELECTOR = new HashMap<>();

        static {
            for (MenuOption o: values()) {
                BY_SELECTOR.put(o.selector, o);
            }
        }

        private MenuOption(String label, char selector) {
            this.label = label;
            this.selector = selector;
        }

        public String option() {
            return this.selector + ": " + this.label;
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
        Console cons = System.console();
        boolean shouldQuit = false;
        AuthenticationSystem.auth = new Auth(new CredentialsFileRepository());

        while (shouldQuit == false) {
            MenuOption selectedOption = getSelectedMenuOption();

            switch (selectedOption) {
                case LOGIN:
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

    private static MenuOption getSelectedMenuOption() {
        Scanner scnr = new Scanner(System.in);
        MenuOption selectedOption = null;

        while (selectedOption == null) {
            char selection;

            showMenu();

            selection = scnr.next().charAt(0);
            selectedOption = MenuOption.valueOfSelector(selection);

            if (selectedOption == null) {
                System.out.println(
                        String.format(
                                "\nOption \"%s\" was not a valid selection.",
                                selection
                        )
                );
            }
        }

        return selectedOption;
    }

    private static void showMenu() {
        MenuOption[] options = auth.isLoggedIn()
                ? MenuOption.authenticatedOptions()
                : MenuOption.unauthenticatedOptions();

        System.out.println("");

        for (MenuOption o: options) {
            System.out.println(o.option());
        }

        System.out.print("Choose an option: ");
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
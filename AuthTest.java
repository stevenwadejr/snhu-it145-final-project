public class AuthTest {

    public static void main(String[] args) {
        System.out.println("Testing for Auth started");

        testUserNotFound();
        testTooManyFailedLoginAttempts();
        testLoginSucceeds();

        System.out.println("Testing completed");
    }

    private static void testUserNotFound() {
        Auth auth = new Auth(getNotFoundFoundRepository());

        try {
            assert (auth.isLoggedIn() == false) : "Failed to assert initial logged in check is false";

            auth.login("foo", "bar");

            assert (auth.isLoggedIn() == false) : "Failed to assert post login \"is logged in\" check is false";
        } catch (Exception e) {
            System.out.println("Test method failed");
        }

    }

    private static void testTooManyFailedLoginAttempts() {
        Auth auth = new Auth(getNotFoundFoundRepository());

        try {
            auth.login("foo", "bar");
            auth.login("foo", "bar");
            auth.login("foo", "bar");

            assert (false == true) : "Failed to assert failure when max login attempts reached";
        } catch (Exception e) {
            assert (e.getMessage().equals("Too many failed login attempts"))
                    : "Failed asserting the given exception was of one of too many login attempts";
        }
    }

    private static void testLoginSucceeds() {
        String givenUsername = "foo";
        String givenPassword = "bar";
        String givenRole = "tester";

        AuthRepository staticRepo = new AuthRepository() {
            @Override
            public String[] findUserRecord(String username, String passwordHash) throws Exception {
                return new String[]{givenUsername, givenPassword, givenRole};
            }
        };

        Auth auth = new Auth(staticRepo);

        try {
            assert (auth.isLoggedIn() == false) : "Failed to assert initial logged in check is false";

            auth.login(givenUsername, givenPassword);

            assert (auth.isLoggedIn() == true) : "Failed to assert post login \"is logged in\" check is false";

            assert auth.getUser().getUsername().equals(givenUsername) : "Failed asserting usernames match";
            assert auth.getUser().getPassword().equals(givenPassword) : "Failed asserting passwords match";
            assert auth.getUser().getRole().equals(givenRole) : "Failed asserting roles match";
        } catch (Exception e) {
            System.out.println("Test method failed");
        }
    }

    private static AuthRepository getNotFoundFoundRepository() {
        AuthRepository notFound = new AuthRepository() {
            @Override
            public String[] findUserRecord(String username, String passwordHash) throws Exception {
                return new String[0];
            }
        };

        return notFound;
    }
}
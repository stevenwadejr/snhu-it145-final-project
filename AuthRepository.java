public interface AuthRepository {

    public String[] findUserRecord(String username, String passwordHash) throws Exception;
}
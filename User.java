import java.io.Serializable;

/**
 * Represents a user account in the media vault system.
 *
 * Relationships:
 * - Has a Library object, where the user's media entries are stored.
 * - Is serializable, allowing user account data to be saved and loaded.
 *
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private Library library;

    /**
     * Constructor for the User class.
     * Initializes the user's account credentials and
     * instantiates a brand new library for the user.
     *
     * @param username the username of the user's account
     * @param password the password of the user's account
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.library =  new Library();
    }

    /**
     * Retrieves the username of the user's account
     *
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retrieves the password of the user's account
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Retrieves the user's library of media entries
     *
     * @return the user's library
     */
    public Library getLibrary() {
        return library;
    }

    /**
     * Updates the password of the user's account
     *
     * @param password the new password
     */
    public void setPassword(String password){
        this.password = password;
    }
}

// Supports the login gate in Driver.java

/**
 * A login account, separate from Customer/Staff business records.
 * Used only to gate access to the system (sign in / register account).
 */
public class Account {
    private String username;
    private String password;
    private AccountRole role;

    public Account(String username, String password, AccountRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}

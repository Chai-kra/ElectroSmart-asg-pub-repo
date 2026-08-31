// Supports the login gate in Driver.java

/**
 * A login account, separate from Customer/Staff business records.
 * Used only to gate access to the system (sign in / register account).
 */
public class Account {
    private String username;
    private String password;
    private AccountRole role;
    private String staffID;     // links this login to a Staff record

    public Account(String username, String password, AccountRole role, String staffID) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.staffID = staffID;     //zq
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

    public String getStaffID() { 
        return staffID; 
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

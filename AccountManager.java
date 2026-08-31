import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private List<Account> accounts = new ArrayList<>();

    public void registerAccount(String username, String password, AccountRole role, String staffID)
            throws DuplicateAccountException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (findByUsername(username) != null) {
            throw new DuplicateAccountException("Username " + username + " is already taken.");
        }
        accounts.add(new Account(username, password, role, staffID));
    }

    public Account login(String username, String password) {
        for (Account acc : accounts) {
            if (acc.getUsername().equalsIgnoreCase(username) && acc.getPassword().equals(password)) {
                return acc;
            }
        }
        return null;
    }

    public List<Account> getAllAccounts() {
        return accounts;
    }

    public void clearAccounts() {
        accounts.clear();
    }
}
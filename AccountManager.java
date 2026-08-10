import java.util.ArrayList;
import java.util.List;


public class AccountManager {
    private List<Account> accounts = new ArrayList<>();

    /** Registers a new login account. Username must be unique. */
    public void registerAccount(String username, String password, AccountRole role)
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
        accounts.add(new Account(username, password, role));
    }

    public Account findByUsername(String username) {
        for (Account a : accounts) {
            if (a.getUsername().equals(username)) {
                return a;
            }
        }
        return null;
    }

    /** Returns the matching account if the username/password pair is correct, else null. */
    public Account login(String username, String password) {
        Account account = findByUsername(username);
        if (account != null && account.getPassword().equals(password)) {
            return account;
        }
        return null;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    /** Admin-only: wipes every login account. */
    public void clearAll() {
        accounts.clear();
    }
}

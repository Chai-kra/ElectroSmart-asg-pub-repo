import java.util.ArrayList;
import java.util.List;

public class AccountManager {
    private List<Account> accounts = new ArrayList<>();

    public void registerAccount(String username, String password, AccountRole role) throws DuplicateAccountException {
        for (Account acc : accounts) {
            if (acc.getUsername().equalsIgnoreCase(username)) {
                throw new DuplicateAccountException("Account with username '" + username + "' already exists.");
            }
        }
        accounts.add(new Account(username, password, role));
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
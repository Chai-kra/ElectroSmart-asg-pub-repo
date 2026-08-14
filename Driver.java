import java.util.Scanner;

public class Driver {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AccountManager accountManager = new AccountManager();
        CustomerManager customerManager = new CustomerManager();

        seedSampleAccounts(accountManager);

        Account currentAccount = null;
        boolean exitProgram = false;

        while (!exitProgram) {
            // ---- Gate: only sign in or register an account is available here ----
            if (currentAccount == null) {
                currentAccount = runGate(scanner, accountManager);
                if (currentAccount == null) {
                    exitProgram = true; // user chose to exit at the gate
                }
                continue;
            }

            // ---- Signed in: full menu, plus data management for admins ----
            System.out.println("\n=== ElectroSmart Appliance Management System ===");
            System.out.println("Signed in as: " + currentAccount);
            System.out.println("1. Register customer");
            System.out.println("2. Register staff");
            System.out.println("3. Add appliance to inventory");
            System.out.println("4. Process an appliance sale");
            System.out.println("5. Extend appliance warranty");
            System.out.println("6. View low-stock warnings");
            System.out.println("7. Search warranty profile (by Customer ID or Serial Number)");
            System.out.println("8. Generate sales report");
            if (currentAccount.getRole() == AccountRole.ADMIN) {
                System.out.println("9. Manage data (view / edit / clear) [ADMIN]");
            }
            System.out.println("L. Log out");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice.toUpperCase()) {
                case "1":
                    registerCustomer(scanner,customerManager);
                    break;
                case "2":
                    registerStaff(scanner);
                    break;
                case "3":
                    addAppliance(scanner);
                    break;
                case "4":
                    processSale(scanner);
                    break;
                case "5":
                    extendWarranty(scanner);
                    break;
                case "6":
                    viewLowStock();
                    break;
                case "7":
                    searchWarranty(scanner);
                    break;
                case "8":
                    generateSalesReport();
                    break;
                case "9":
                    if (currentAccount.getRole() == AccountRole.ADMIN) {
                        manageData(scanner, customerManager);
                    } else {
                        System.out.println("Invalid option, please try again.");
                    }
                    break;
                case "L":
                    System.out.println("Logged out.");
                    currentAccount = null;
                    break;
                case "0":
                    exitProgram = true;
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
        System.out.println("Goodbye!");
        scanner.close();
    }

    // =========================================================
    // Gate: sign in or register an account. Nothing else is reachable
    // until one of these succeeds. Fully implemented, not a placeholder.
    // =========================================================
    private static Account runGate(Scanner scanner, AccountManager accountManager) {
        while (true) {
            System.out.println("\n=== Welcome to ElectroSmart ===");
            System.out.println("1. Sign in");
            System.out.println("2. Register account");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                System.out.print("Username: ");
                String username = scanner.nextLine().trim();
                System.out.print("Password: ");
                String password = scanner.nextLine().trim();
                Account account = accountManager.login(username, password);
                if (account == null) {
                    System.out.println("Invalid username or password.");
                } else {
                    System.out.println("Signed in successfully.");
                    return account;
                }
            } else if (choice.equals("2")) {
                System.out.print("Choose a username: ");
                String username = scanner.nextLine().trim();
                System.out.print("Choose a password: ");
                String password = scanner.nextLine().trim();
                System.out.print("Role (ADMIN/STAFF): ");
                String roleInput = scanner.nextLine().trim().toUpperCase();
                try {
                    AccountRole role = AccountRole.valueOf(roleInput);
                    accountManager.registerAccount(username, password, role);
                    System.out.println("Account registered. Please sign in.");
                } catch (IllegalArgumentException | DuplicateAccountException e) {
                    System.out.println("Could not register account: " + e.getMessage());
                }
            } else if (choice.equals("0")) {
                return null;
            } else {
                System.out.println("Invalid option, please try again.");
            }
        }
    }

    /** Sample login accounts only, for testing the gate. admin/admin123 (ADMIN), staff1/staff123 (STAFF). */
    private static void seedSampleAccounts(AccountManager accountManager) {
        try {
            accountManager.registerAccount("admin", "admin123", AccountRole.ADMIN);
            accountManager.registerAccount("staff1", "staff123", AccountRole.STAFF);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to seed sample accounts: " + e.getMessage(), e);
        }
    }

    // =========================================================
    // Day-to-day actions - placeholders. Wire each of these up to
    // CustomerManager / StaffManager / StoreManager as that module is
    // implemented.
    // =========================================================
    private static void registerCustomer(Scanner scanner, CustomerManager customerManager) {
    System.out.println("\n--- Register New Customer ---");

    String customerID;
    while (true) {
        System.out.print("Enter Customer ID (e.g. CUS001): ");
        customerID = scanner.nextLine().trim();
        if (customerID.isEmpty()) {
            System.out.println("Customer ID cannot be empty.");
        } else if (customerManager.findByID(customerID) != null) {
            System.out.println("This Customer ID already exists.");
        } else {22
            break;
        }
    }

    System.out.print("Enter Name: ");
    String name = scanner.nextLine().trim();

    String email;
    while (true) {
        System.out.print("Enter Email: ");
        email = scanner.nextLine().trim();
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            System.out.println("Invalid email format.");
        } else {
            break;
        }
    }

    MembershipStatus status = null;
    while (status == null) {
        System.out.print("Membership Status (REGULAR/SILVER/GOLD): ");
        try {
            status = MembershipStatus.valueOf(scanner.nextLine().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status. Try REGULAR, SILVER, or GOLD.");
        }
    }

    try {
        Customer newCustomer = new Customer(customerID, name, email, status);
        customerManager.addCustomer(newCustomer);
        System.out.println("Customer registered successfully: " + newCustomer);
    } catch (DuplicateCustomerException | IllegalArgumentException e) {
        System.out.println("Could not register customer: " + e.getMessage());
    }
}

    private static void registerStaff(Scanner scanner) {
        // TODO: prompt for name, role, email, annual salary and call
        // StaffManager.registerStaff(...). See Staff module.
        System.out.println("TODO: Register staff is not implemented yet.");
    }

    private static void addAppliance(Scanner scanner) {
        // TODO: prompt for appliance category/fields and call
        // StoreManager.addAppliance(new WhiteGoods(...) / new DigitalGadgets(...)).
        // See Module C.
        System.out.println("TODO: Add appliance is not implemented yet.");
    }

    private static void processSale(Scanner scanner) {
        // TODO: prompt for applianceID, customerID, quantity, staffID and
        // call StoreManager.processSale(...).
        System.out.println("TODO: Process sale is not implemented yet.");
    }

    private static void extendWarranty(Scanner scanner) {
        // TODO: prompt for applianceID, extraMonths, staffID and call
        // StoreManager.extendApplianceWarranty(...). See Module D.
        System.out.println("TODO: Extend warranty is not implemented yet.");
    }

    private static void viewLowStock() {
        // TODO: call StoreManager.viewLowStockWarnings() and print the results.
        System.out.println("TODO: View low-stock warnings is not implemented yet.");
    }

    private static void searchWarranty(Scanner scanner) {
        // TODO: prompt for a Customer ID or Serial Number and call
        // StoreManager.searchWarrantyProfile(...).
        System.out.println("TODO: Search warranty profile is not implemented yet.");
    }

    private static void generateSalesReport() {
        // TODO: call StoreManager.generateSalesReport() and print it.
        System.out.println("TODO: Generate sales report is not implemented yet.");
    }

    // =========================================================
    // Admin-only data management - placeholders.
    // =========================================================
    private static void manageData(Scanner scanner,CustomerManager customerManager) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Manage Data (Admin) ---");
            System.out.println("1. View all data");
            System.out.println("2. Edit a customer");
            System.out.println("3. Edit a staff member");
            System.out.println("4. Edit an appliance");
            System.out.println("5. Clear all customers");
            System.out.println("6. Clear all staff");
            System.out.println("7. Clear all appliances (inventory)");
            System.out.println("8. Clear all transactions");
            System.out.println("9. Clear all login accounts");
            System.out.println("0. Back");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // TODO: print every Customer/Staff/Appliance/Transaction/Account using their getters.
                    System.out.println("TODO: View all data is not implemented yet.");
                    break;
                case "2":
                    // TODO: look up a Customer by ID and update fields via its setters.
                    System.out.println("TODO: Edit a customer is not implemented yet.");
                    break;
                case "3":
                    // TODO: look up a Staff member by ID and update fields via its setters.
                    System.out.println("TODO: Edit a staff member is not implemented yet.");
                    break;
                case "4":
                    // TODO: look up an Appliance by ID and update fields via its setters.
                    System.out.println("TODO: Edit an appliance is not implemented yet.");
                    break;
                case "5":
                    // TODO: call CustomerManager.clearAll().
                    System.out.println("TODO: Clear all customers is not implemented yet.");
                    break;
                case "6":
                    // TODO: call StaffManager.clearAll().
                    System.out.println("TODO: Clear all staff is not implemented yet.");
                    break;
                case "7":
                    // TODO: call StoreManager.clearInventory().
                    System.out.println("TODO: Clear all appliances is not implemented yet.");
                    break;
                case "8":
                    // TODO: call StoreManager.clearTransactions().
                    System.out.println("TODO: Clear all transactions is not implemented yet.");
                    break;
                case "9":
                    // TODO: call AccountManager.clearAll().
                    System.out.println("TODO: Clear all login accounts is not implemented yet.");
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }
}

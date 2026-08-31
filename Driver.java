import java.util.List;
import java.util.Scanner;

public class Driver {
    private static final String STAFF_KEY = "123aaa";

    private static final String ORANGE_YELLOW_BANNER = 
        "\u001B[38;5;202m ___________.__                 __                 _________                      __   \n" +
        "\u001B[38;5;208m \\_   _____/|  |   ____   _____/  |________  ____ /   _____/ _____ _____ ________/  |_ \n" +
        "\u001B[38;5;214m  |    __)_ |  | _/ __ \\_/ ___\\   __\\_  __ \\/  _ \\\\_____  \\ /     \\\\__  \\\\_  __ \\   __\\\n" +
        "\u001B[38;5;220m  |        \\|  |_\\  ___/\\  \\___|  |  |  | \\(  <_> )        \\  Y Y  \\/ __ \\|  | \\/|  |  \n" +
        "\u001B[38;5;226m /_______  /|____/\\___  >\\___  >__|  |__|   \\____/_______  /__|_|  (____  /__|   |__|  \n" +
        "\u001B[38;5;228m         \\/           \\/     \\/                          \\/      \\/     \\/          \u001B[0m";

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void pause(Scanner scanner) {
        System.out.println("\nPress Enter to return to the menu...");
        scanner.nextLine();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AccountManager accountManager = new AccountManager();
        CustomerManager customerManager = new CustomerManager();
        ApplianceManager applianceManager = new ApplianceManager();
        applianceManager.loadSampleData();
        StaffManager staffManager = new StaffManager();

        seedSampleAccounts(accountManager, staffManager);

        Account currentAccount = null;
        boolean exitProgram = false;

        while (!exitProgram) {
            clearScreen();
            if (currentAccount == null) {
                // CHANGED: now passes staffManager so registration can create the linked Staff record
                currentAccount = runGate(scanner, accountManager, staffManager);
                if (currentAccount == null) {
                    exitProgram = true;
                }
                continue;
            }

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
            System.out.println("R. View a staff member's sales report");
            System.out.println("L. Log out");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice.toUpperCase()) {
                case "1":
                    registerCustomer(scanner, customerManager);
                    pause(scanner);
                    break;
                case "2":
                    registerStaff(scanner, staffManager);
                    pause(scanner);
                    break;
                case "3":
                    try {
                        applianceManager.addAppliance(scanner);
                    } catch (DuplicateApplianceException e) {
                        System.out.println("Error: " + e.getMessage());
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    pause(scanner);
                    break;
                case "4": {
                    Staff staff = staffManager.findByID(currentAccount.getStaffID());
                    if (staff == null) {
                        System.out.println("No staff profile linked to this account yet.");
                        System.out.println("Please register your staff profile first (option 2), using Staff ID: " + currentAccount.getStaffID());
                        pause(scanner);
                        break;
                    }
                    try {
                        applianceManager.processSale(scanner, customerManager, staffManager, staff);
                    } catch (InvalidWarrantyExtensionException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    pause(scanner);
                    break;
                }
                case "5": {
                    Staff staff = staffManager.findByID(currentAccount.getStaffID());
                    if (staff == null) {
                        System.out.println("No staff profile linked to this account yet.");
                        System.out.println("Please register your staff profile first (option 2), using Staff ID: " + currentAccount.getStaffID());
                        pause(scanner);
                        break;
                    }
                    try {
                        applianceManager.extendApplianceWarranty(scanner, staff);
                    } catch (InvalidWarrantyExtensionException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    pause(scanner);
                    break;
                }
                case "6":
                    applianceManager.viewLowStock(scanner);
                    pause(scanner);
                    break;
                case "7":
                    searchWarrantyProfile(scanner, applianceManager, staffManager);
                    pause(scanner);
                    break;
                case "8":
                    generateSalesReport();
                    pause(scanner);
                    break;
                case "9":
                    if (currentAccount.getRole() == AccountRole.ADMIN) {
                        manageData(scanner, customerManager, staffManager, applianceManager, accountManager);
                    } else {
                        System.out.println("Invalid option, please try again.");
                    }
                    break;
                case "R":
                    viewStaffSalesReport(scanner, staffManager);
                    pause(scanner);
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

    // CHANGED: added StaffManager parameter
    private static Account runGate(Scanner scanner, AccountManager accountManager, StaffManager staffManager) {
        while (true) {
            System.out.println("\n" + ORANGE_YELLOW_BANNER);
            System.out.println("==================================================================");
            System.out.println("               ELECTROSMART APPLIANCE MANAGEMENT                   ");
            System.out.println("==================================================================");
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
                // CHANGED: was inline registration of Account only; now delegates to
                // registerAccountAndStaff() which also creates the linked Staff record
                registerAccountAndStaff(scanner, accountManager, staffManager);
            } else if (choice.equals("0")) {
                return null;
            } else {
                System.out.println("Invalid option, please try again.");
            }
        }
    }

    /**
     * CHANGED (new method): Registers a login Account AND its linked Staff profile
     * together, in one flow. Previously these were two separate steps — register an
     * account here, then separately register a Staff record from the main menu with
     * a matching ID — which meant a brand-new account couldn't process sales or
     * extend warranties until you went and did a second, unrelated-looking step.
     * This replaces the old inline registration block that used to live in runGate().
     */
    private static void registerAccountAndStaff(Scanner scanner, AccountManager accountManager, StaffManager staffManager) {
        System.out.print("Enter Staff Key to register: ");
        String inputKey = scanner.nextLine().trim();
        if (!STAFF_KEY.equals(inputKey)) {
            System.out.println("Access Denied: Invalid Staff Key.");
            return;
        }

        String username;
        while (true) {
            System.out.print("Choose a username: ");
            username = scanner.nextLine().trim();
            if (username.isEmpty()) {
                System.out.println("Username cannot be empty.");
            } else if (accountManager.findByUsername(username) != null) {
                System.out.println("That username is already taken.");
            } else {
                break;
            }
        }

        System.out.print("Choose a password: ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            System.out.println("Password cannot be empty. Registration cancelled.");
            return;
        }

        AccountRole role = null;
        while (role == null) {
            System.out.print("Role (ADMIN/STAFF): ");
            try {
                role = AccountRole.valueOf(scanner.nextLine().trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid role. Please enter ADMIN or STAFF.");
            }
        }

        System.out.println("\nNow let's set up your Staff profile, so your account is ready to use right away.");
        String staffID;
        while (true) {
            System.out.print("Choose a Staff ID (e.g. STF001): ");
            staffID = scanner.nextLine().trim();
            if (staffID.isEmpty()) {
                System.out.println("Staff ID cannot be empty.");
            } else if (staffManager.findByID(staffID) != null) {
                System.out.println("This Staff ID is already in use. Choose another.");
            } else {
                break;
            }
        }

        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Job Title (e.g. Sales Associate, Manager): ");
        String jobTitle = scanner.nextLine().trim();
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
        double annualSalary;
        while (true) {
            System.out.print("Enter Annual Salary: ");
            String salaryInput = scanner.nextLine().trim();
            try {
                annualSalary = Double.parseDouble(salaryInput);
                if (annualSalary < 0) {
                    System.out.println("Annual salary cannot be negative.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number — please enter digits only (e.g. 45000 or 45000.50).");
            }
        }

        try {
            Staff newStaff = new Staff(staffID, name, jobTitle, email, annualSalary);
            staffManager.registerStaff(newStaff);
            accountManager.registerAccount(username, password, role, staffID);
            System.out.println("\nAccount and Staff profile registered successfully. Please sign in.");
        } catch (DuplicateStaffException | DuplicateAccountException | IllegalArgumentException e) {
            System.out.println("Could not complete registration: " + e.getMessage());
        }
    }

    private static void searchWarrantyProfile(Scanner scanner, ApplianceManager applianceManager, StaffManager staffManager) {
        System.out.println("\nSearch Warranty Profile:");
        System.out.println("1. By Customer ID");
        System.out.println("2. By Serial Number (Appliance ID)");
        System.out.print("Choose search type: ");
        String choice = scanner.nextLine();

        if (choice.equals("1")) {
            System.out.print("Enter Customer ID: ");
            String customerID = scanner.nextLine();
            List<Transaction> sales = staffManager.getSalesByCustomer(customerID);
            if (sales.isEmpty()) {
                System.out.println("No purchase/warranty records found for Customer ID \"" + customerID + "\".");
                return;
            }
            System.out.println("\n=== Warranty Records for Customer " + customerID + " ===");
            for (Transaction t : sales) {
                printWarrantyDetails(applianceManager, t.getApplianceID());
            }
        } else if (choice.equals("2")) {
            System.out.print("Enter Serial Number (Appliance ID): ");
            String serial = scanner.nextLine();
            printWarrantyDetails(applianceManager, serial);
        } else {
            System.out.println("Invalid choice.");
        }
    }

    private static void printWarrantyDetails(ApplianceManager applianceManager, String applianceID) {
        Appliance appliance = applianceManager.findApplianceByID(applianceID);
        if (appliance == null) {
            System.out.println("No appliance found with Serial Number \"" + applianceID + "\".");
            return;
        }
        if (!appliance.hasWarranty()) {
            System.out.println(appliance.getModelName() + " (" + applianceID + ") - No active warranty.");
            return;
        }
        Warranty w = appliance.getWarranty();
        System.out.println("\n" + appliance.getModelName() + " (" + applianceID + ")");
        System.out.println("  Warranty ID: " + w.getWarrantyID());
        System.out.println("  Provider: " + w.getProvider());
        System.out.println("  Duration: " + w.getDurationMonths() + " months");
        System.out.println("  Extended: " + (w.isExtended() ? "Yes" : "No"));
        System.out.println("  Handled By: " + (w.getHandledBy() != null ? w.getHandledBy().getName() : "N/A"));
    }

    private static void seedSampleAccounts(AccountManager accountManager, StaffManager staffManager) {
        try {
            staffManager.registerStaff(new Staff("ADM001", "Admin", "Administrator", "admin@electrosmart.com", 60000));
            accountManager.registerAccount("Admin", "123456", AccountRole.ADMIN, "ADM001");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to seed sample accounts: " + e.getMessage(), e);
        }
    }

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
            } else {
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

    private static void registerStaff(Scanner scanner, StaffManager staffManager) {
        System.out.println("\n--- Register New Staff ---");
        String staffID;
        while (true) {
            System.out.print("Enter Staff ID (e.g. STF001): ");
            staffID = scanner.nextLine().trim();
            if (staffID.isEmpty()) {
                System.out.println("Staff ID cannot be empty.");
            } else if (staffManager.findByID(staffID) != null) {
                System.out.println("This Staff ID already exists.");
            } else {
                break;
            }
        }
        System.out.print("Enter Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Role (e.g. Sales Associate, Manager): ");
        String role = scanner.nextLine().trim();
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
        double annualSalary;
        while (true) {
            System.out.print("Enter Annual Salary: ");
            String salaryInput = scanner.nextLine().trim();
            try {
                annualSalary = Double.parseDouble(salaryInput);
                if (annualSalary < 0) {
                    System.out.println("Annual salary cannot be negative.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number — please enter digits only (e.g. 45000 or 45000.50).");
            }
        }
        try {
            Staff newStaff = new Staff(staffID, name, role, email, annualSalary);
            staffManager.registerStaff(newStaff);
            System.out.println("Staff registered successfully: " + newStaff);
        } catch (DuplicateStaffException | IllegalArgumentException e) {
            System.out.println("Could not register staff: " + e.getMessage());
        }
    }

    private static void viewStaffSalesReport(Scanner scanner, StaffManager staffManager) {
        System.out.print("\nEnter Staff ID to view their sales report: ");
        String staffID = scanner.nextLine().trim();
        Staff staff = staffManager.findByID(staffID);
        if (staff == null) {
            System.out.println("No staff found with ID " + staffID);
            return;
        }
        System.out.println("Sales report for " + staff);
        List<Transaction> sales = staffManager.getSalesByStaff(staffID);
        if (sales.isEmpty()) {
            System.out.println("No sales recorded for this staff member yet.");
        } else {
            for (Transaction t : sales) {
                System.out.println("  " + t);
            }
        }
    }

    private static void generateSalesReport() {
        System.out.println("TODO: Generate sales report is not implemented yet.");
    }

    // UNCHANGED: left exactly as original — still TODO stubs, not touched
    private static void manageData(Scanner scanner, CustomerManager customerManager, 
                                StaffManager staffManager, ApplianceManager applianceManager, 
                                AccountManager accountManager) {

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
                case "2": 
                case "3": 
                case "4":
                case "5": 
                case "6": 
                case "7": 
                case "8": 
                case "9":
                    System.out.println("TODO: Admin management option " + choice + " is not implemented yet.");
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
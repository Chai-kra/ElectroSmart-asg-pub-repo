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
        System.out.print("\nPress Enter to return to the menu ~");
        String input = scanner.nextLine().trim();
        if (input.equalsIgnoreCase("exit")) {
            System.out.println("Goodbye!");
            System.exit(0);
        }
    }

    private static String readAlphabetOnly(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("This field cannot be empty.");
            } else if (!input.matches("[A-Za-z ]+")) {
                System.out.println("Only alphabet letters are allowed (no numbers or symbols).");
            } else {
                return input;
            }
        }
    }

    private static String readValidEmail(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.matches(InputValidator.EMAIL_REGEX)) {
                System.out.println("Invalid email format — expected something like name@example.com.");
            } else {
                return input;
            }
        }
    }
    
    private static double readNonNegativeDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(input);
                if (value < 0) {
                    System.out.println("Value cannot be negative.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number — please enter digits only (e.g. 45000 or 45000.50).");
            }
        }
    }
    private static final String ROLE_MANAGER = "Manager";
    private static final String ROLE_STAFF = "Staff";
    private static String readStaffRole(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equals(ROLE_MANAGER) || input.equals(ROLE_STAFF)) {
                return input;
            }
            System.out.println("Invalid role — must be exactly \"Manager\" or \"Staff\" (case-sensitive).");
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AccountManager accountManager = new AccountManager();
        CustomerManager customerManager = new CustomerManager();
        ApplianceManager applianceManager = new ApplianceManager();
        applianceManager.loadSampleData();
        WarrantyManager warrantyManager = new WarrantyManager(applianceManager);
        StaffManager staffManager = new StaffManager();

        seedSampleAccounts(accountManager, staffManager);
        customerManager.loadSampleData();
        staffManager.loadSampleData();
        seedSampleTransactions(applianceManager, customerManager, staffManager);
        Account currentAccount = null;
        boolean exitProgram = false;

        while (!exitProgram) {
            clearScreen();
            if (currentAccount == null) {

                currentAccount = runGate(scanner, accountManager, staffManager);
                if (currentAccount == null) {
                    exitProgram = true;
                }
                continue;
            }

            System.out.println("\n=== ElectroSmart Appliance Management System ===");
            System.out.println("Signed in as: " + currentAccount);
            System.out.println("1. Register");
            System.out.println("2. Appliance");
            System.out.println("3. Inventory");
            System.out.println("4. Generate sales report");
            System.out.println("5. View a staff member's sales report");
            System.out.println("6. View all available data");
            if (currentAccount.getRole() == AccountRole.ADMIN) {
                System.out.println("7. Manage data [ADMIN]");
            }

            System.out.println("L. Log out");
            System.out.println("0. Exit");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice.toUpperCase()) {
                case "1":
                    registerMenu(scanner, customerManager, staffManager);
                    break;
                case "2":
                    applianceMenu(scanner, applianceManager, warrantyManager, customerManager, staffManager, currentAccount);
                    break;
                case "3":
                    inventoryMenu(scanner, applianceManager);
                    break;
                case "4":
                    staffManager.printSalesReport();
                    pause(scanner);
                    break;
                case "5":
                    staffManager.viewStaffSalesReport(scanner);
                    pause(scanner);
                    break;

                case "6":
                    viewAllAvailableData(staffManager, applianceManager, customerManager);
                    pause(scanner);
                    break;
                case "7":

                    if (currentAccount.getRole() == AccountRole.ADMIN) {
                        manageData(scanner, customerManager, staffManager, applianceManager);
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
    private static Account runGate(Scanner scanner, AccountManager accountManager, StaffManager staffManager) {
        while (true) {
            clearScreen();
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
                    pause(scanner);
                } else {
                    System.out.println("Signed in successfully.");
                    return account;
                }
            } else if (choice.equals("2")) {
                registerAccountAndStaff(scanner, accountManager, staffManager);
                pause(scanner);
            } else if (choice.equals("0")) {
                return null;
            } else {
                System.out.println("Invalid option, please try again.");
            }
        }
    }
    private static void registerMenu(Scanner scanner, CustomerManager customerManager, StaffManager staffManager) {
        boolean back = false;
        while (!back) {
            clearScreen();
            System.out.println("\n--- Register ---");
            System.out.println("1. Register customer");
            System.out.println("2. Register staff");
            System.out.println("0. Back");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    registerCustomer(scanner, customerManager);
                    pause(scanner);
                    break;
                case "2":
                    staffManager.registerStaff(scanner);
                    pause(scanner);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }
    private static void applianceMenu(Scanner scanner, ApplianceManager applianceManager, WarrantyManager warrantyManager,
                                       CustomerManager customerManager, StaffManager staffManager, Account currentAccount) {
        boolean back = false;
        while (!back) {
            clearScreen();
            System.out.println("\n--- Appliance ---");
            System.out.println("1. Add appliance");
            System.out.println("2. Process appliance sale");
            System.out.println("3. Warranty detail (by Customer ID or Appliance ID)");
            System.out.println("4. Extend warranty");
            System.out.println("5. File a warranty claim");
            System.out.println("6. View & settle warranty claims");
            System.out.println("0. Back");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    try {
                        applianceManager.addAppliance(scanner);
                    } catch (DuplicateApplianceException | IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    pause(scanner);
                    break;
                case "2": {
                    Staff staff = staffManager.findByID(currentAccount.getStaffID());
                    if (staff == null) {
                        System.out.println("No staff profile linked to this account yet.");
                        System.out.println("Please register your staff profile first (Register > Register staff), using Staff ID: " + currentAccount.getStaffID());
                        pause(scanner);
                        break;
                    }
                    try {
                        warrantyManager.processSale(scanner, customerManager, staffManager, staff);
                    } catch (InvalidWarrantyExtensionException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    pause(scanner);
                    break;
                }
                case "3":
                    searchWarrantyProfile(scanner, applianceManager, staffManager);
                    pause(scanner);
                    break;
                case "4": {
                    Staff staff = staffManager.findByID(currentAccount.getStaffID());
                    if (staff == null) {
                        System.out.println("No staff profile linked to this account yet.");
                        System.out.println("Please register your staff profile first (Register > Register staff), using Staff ID: " + currentAccount.getStaffID());
                        pause(scanner);
                        break;
                    }
                    try {
                        warrantyManager.extendApplianceWarranty(scanner, staff);
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
                        System.out.println("Please register your staff profile first (Register > Register staff), using Staff ID: " + currentAccount.getStaffID());
                        pause(scanner);
                        break;
                    }
                    warrantyManager.fileWarrantyClaim(scanner, staff);
                    pause(scanner);
                    break;
                }
                case "6": {
                    Staff staff = staffManager.findByID(currentAccount.getStaffID());
                    if (staff == null) {
                        System.out.println("No staff profile linked to this account yet.");
                        System.out.println("Please register your staff profile first (Register > Register staff), using Staff ID: " + currentAccount.getStaffID());
                        pause(scanner);
                        break;
                    }
                    warrantyManager.manageWarrantyClaims(scanner, staff);
                    pause(scanner);
                    break;
                }
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }
    private static void inventoryMenu(Scanner scanner, ApplianceManager applianceManager) {
        boolean back = false;
        while (!back) {
            clearScreen();
            System.out.println("\n--- Inventory ---");
            System.out.println("1. View low stock warnings");
            System.out.println("2. View inventory");
            System.out.println("0. Back");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    applianceManager.viewLowStock(scanner);
                    break;
                case "2":
                    applianceManager.displayInventory();
                    pause(scanner);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
            }
        }
    }
    private static void viewAllAvailableData(StaffManager staffManager, ApplianceManager applianceManager,
                                              CustomerManager customerManager) {
        System.out.println("\n=== All Staff Members ===");
        staffManager.displayStaffTable();
        System.out.println("\n=== All Customers ===");
        List<Customer> customerList = customerManager.getAllCustomers();
        if (customerList.isEmpty()) {
            System.out.println("No customers registered yet.");
        } else {
            printCustomerTable(customerList);
            printMembershipBreakdown(customerManager.getMembershipBreakdown());
        }
        System.out.println("\n=== All Appliances ===");
        if (applianceManager.getAllAppliances().isEmpty()) {
            System.out.println("No appliances in inventory yet.");
        } else {
            applianceManager.displayInventory();
        }
    }

    private static void printCustomerTable(List<Customer> customerList) {
        System.out.printf("%-8s %-18s %-11s %-26s %-11s %-9s %s%n",
                "ID", "Name", "Category", "Email", "Membership", "Discount", "Details");
        for (Customer c : customerList) {
            System.out.printf("%-8s %-18s %-11s %-26s %-11s %-9s %s%n",
                    c.getCustomerID(), c.getName(), c.getCustomerCategory(), c.getEmail(),
                    c.getMemberShipStatus(), (int) (c.getDiscountRate() * 100) + "%", c.getExtraInfo());
        }
    }
    
    private static void printMembershipBreakdown(int[] counts) {
        MembershipStatus[] tiers = MembershipStatus.values();
        System.out.print("Membership breakdown: ");
        for (int i = 0; i < counts.length; i++) {
            System.out.print(tiers[i] + "=" + counts[i]);
            if (i < counts.length - 1) System.out.print(", ");
        }
        System.out.println();
    }
private static void seedSampleTransactions(ApplianceManager applianceManager, CustomerManager customerManager,
                                            StaffManager staffManager) {
    recordSampleSale(applianceManager, customerManager, staffManager, "A001", "CUS001", "STF001", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A005", "CUS002", "STF002", 2);
    recordSampleSale(applianceManager, customerManager, staffManager, "A003", "CUS003", "STF001", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A001", "CUS002", "STF002", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A005", "CUS001", "STF001", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A002", "CUS004", "STF003", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A004", "CUS005", "STF003", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A006", "CUS006", "STF003", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A009", "CUS007", "STF004", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A009", "CUS008", "STF004", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A007", "CUS009", "STF004", 2);
    recordSampleSale(applianceManager, customerManager, staffManager, "A008", "CUS001", "STF005", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A008", "CUS002", "STF005", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A003", "CUS003", "STF005", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A003", "CUS004", "STF001", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A001", "CUS005", "STF001", 2);
    recordSampleSale(applianceManager, customerManager, staffManager, "A001", "CUS006", "STF002", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A005", "CUS007", "STF002", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A005", "CUS008", "STF002", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A007", "CUS009", "STF003", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A004", "CUS001", "STF004", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A009", "CUS002", "STF005", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A001", "CUS003", "STF001", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A003", "CUS004", "STF002", 1);
    recordSampleSale(applianceManager, customerManager, staffManager, "A005", "CUS005", "STF003", 1);
}
    private static void recordSampleSale(ApplianceManager applianceManager, CustomerManager customerManager,
                                          StaffManager staffManager, String applianceID, String customerID,
                                          String staffID, int quantity) {
        Appliance appliance = applianceManager.findApplianceByID(applianceID);
        Customer customer = customerManager.findByID(customerID);
        Staff staff = staffManager.findByID(staffID);
        if (appliance == null || customer == null || staff == null) {
            return;
        }
        double finalPrice = appliance.calculateFinalPrice() * quantity * (1 - customer.getDiscountRate());
        appliance.reduceStock(quantity);
        appliance.activateWarranty(staff);
        staffManager.recordSale(new Transaction(appliance.getApplianceID(), customer.getCustomerID(), quantity, finalPrice, staff));
    }
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
        String staffID = staffManager.generateNextStaffID();
        System.out.println("Assigned Staff ID: " + staffID);
        String name = readAlphabetOnly(scanner, "Enter Name: ");
        String jobTitle = readStaffRole(scanner, "Enter Job Title (Manager/Staff): ");
        String email = readValidEmail(scanner, "Enter Email: ");
        double annualSalary = readNonNegativeDouble(scanner, "Enter Annual Salary: ");
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
        staffManager.registerStaff(new Staff("ADM001", "Admin", "Manager", "admin@electrosmart.com", 60000));
        accountManager.registerAccount("Admin", "123456", AccountRole.ADMIN, "ADM001");
    } catch (Exception e) {
        throw new IllegalStateException("Failed to seed sample accounts: " + e.getMessage(), e);
    }
}
    private static void registerCustomer(Scanner scanner, CustomerManager customerManager) {
        System.out.println("\n--- Register New Customer ---");
        System.out.println("Customer Type:");
        System.out.println("1. Individual");
        System.out.println("2. Corporate");
        String type;
        while (true) {
            System.out.print("Select an option: ");
            type = scanner.nextLine().trim();
            if (type.equals("1") || type.equals("2")) break;
            System.out.println("Invalid option — please enter 1 or 2.");
        }

        String customerID = customerManager.generateNextCustomerID();
        System.out.println("Assigned Customer ID: " + customerID);
        String name = readAlphabetOnly(scanner, "Enter Name: ");
        String email = readValidEmail(scanner, "Enter Email: ");
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
            Customer newCustomer;
            if (type.equals("1")) {
                String icNumber;
                while (true) {
                    System.out.print("Enter IC Number (format: 990101-14-5566): ");
                    icNumber = scanner.nextLine().trim();
                    if (icNumber.isEmpty()) {
                        System.out.println("IC number cannot be empty.");
                    } else if (!icNumber.matches("\\d{6}-\\d{2}-\\d{4}")) {
                        System.out.println("IC number must follow the format 990101-14-5566 (dashes required).");
                    } else {
                        break;
                    }
                }
                newCustomer = new IndividualCustomer(customerID, name, email, status, icNumber);
            } else {
                String companyName = readAlphabetOnly(scanner, "Enter Company Name: ");
                String contactPerson = readAlphabetOnly(scanner, "Enter Contact Person: ");
                newCustomer = new CorporateCustomer(customerID, name, email, status, companyName, contactPerson);
            }
            customerManager.addCustomer(newCustomer);
            System.out.println("Customer registered successfully: " + newCustomer);
        } catch (DuplicateCustomerException | IllegalArgumentException e) {
            System.out.println("Could not register customer: " + e.getMessage());
        }
    }
    
    private static void editCustomer(Scanner scanner, CustomerManager customerManager) {
        System.out.print("\nEnter Customer ID to edit (0 to cancel): ");
        String id = scanner.nextLine().trim();
        if (id.equals("0")) return;
        Customer customer = customerManager.findByID(id);
        if (customer == null) {
            System.out.println("No customer found with ID \"" + id + "\".");
            return;
        }
        System.out.println("Editing: " + customer);
        System.out.println("1. Name");
        System.out.println("2. Email");
        System.out.println("3. Membership Status");
        System.out.println("0. Cancel");
        System.out.print("Field to edit: ");
        String field = scanner.nextLine().trim();
        try {
            switch (field) {
                case "1":
                    customer.setName(readAlphabetOnly(scanner, "New Name: "));
                    break;
                case "2":
                    customer.setEmail(readValidEmail(scanner, "New Email: "));
                    break;
                case "3": {
                    MembershipStatus status = null;
                    while (status == null) {
                        System.out.print("New Membership Status (REGULAR/SILVER/GOLD): ");
                        try {
                            status = MembershipStatus.valueOf(scanner.nextLine().trim().toUpperCase());
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid status. Try REGULAR, SILVER, or GOLD.");
                        }
                    }
                    customer.setMembershipStatus(status);
                    break;
                }
                case "0":
                    System.out.println("Cancelled.");
                    return;
                default:
                    System.out.println("Invalid option.");
                    return;
            }
            System.out.println("Customer updated: " + customer);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    private static void manageData(Scanner scanner, CustomerManager customerManager,
                                StaffManager staffManager, ApplianceManager applianceManager) {
        boolean back = false;
        while (!back) {
            clearScreen();
            System.out.println("\n--- Manage Data (Admin) ---");
            System.out.println("1. Edit a customer");
            System.out.println("2. Edit a staff member");
            System.out.println("3. Edit an appliance");
            System.out.println("0. Back");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    editCustomer(scanner, customerManager);
                    pause(scanner);
                    break;
                case "2":
                    staffManager.editStaffMember(scanner);
                    pause(scanner);
                    break;
                case "3":
                    applianceManager.editAppliance(scanner);
                    pause(scanner);
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
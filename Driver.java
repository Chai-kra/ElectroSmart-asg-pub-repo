import java.util.List;
import java.util.Scanner;

public class Driver {
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
        
        seedSampleAccounts(accountManager);
        seedSampleStaff(staffManager);

        Account currentAccount = null;
        boolean exitProgram = false;

        while (!exitProgram) {
            clearScreen();
            if (currentAccount == null) {
                currentAccount = runGate(scanner, accountManager);
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
                    } catch (DuplicateApplianceException | IllegalArgumentException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    pause(scanner);
                    break;
                case "4":
                    try {
                        applianceManager.processSale(scanner, customerManager, staffManager);
                    } catch (InvalidWarrantyExtensionException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    pause(scanner);
                    break;
                case "5":
                    try {
                        applianceManager.extendApplianceWarranty(scanner, staffManager);
                    } catch (InvalidWarrantyExtensionException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    pause(scanner);
                    break;
                case "6":
                    applianceManager.viewLowStock(scanner);
                    break;
                case "7":
                    searchWarranty(scanner, staffManager, applianceManager);
                    pause(scanner);
                    break;
                case "8":
                    generateSalesReport(staffManager);
                    pause(scanner);
                    break;
                case "9":
                    if (currentAccount.getRole() == AccountRole.ADMIN) {
                        manageData(scanner, customerManager, staffManager, applianceManager, accountManager);
                    } else {
                        System.out.println("Invalid option, please try again.");
                        pause(scanner);
                    }
                    break;
                case "R":
                    viewStaffSalesReport(scanner, staffManager);
                    pause(scanner);
                    break;
                case "L":
                    System.out.println("Logged out.");
                    currentAccount = null;
                    pause(scanner);
                    break;
                case "0":
                    exitProgram = true;
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
                    pause(scanner);
            }
        }
        System.out.println("Goodbye!");
        scanner.close();
    }

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

    private static void seedSampleAccounts(AccountManager accountManager) {
        try {
            accountManager.registerAccount("admin", "admin123", AccountRole.ADMIN);
            accountManager.registerAccount("staff1", "staff123", AccountRole.STAFF);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to seed sample accounts: " + e.getMessage(), e);
        }
    }

    private static void seedSampleStaff(StaffManager staffManager) {
        try {
            staffManager.registerStaff(new Staff("STF001", "John Doe", "Sales Associate", "john@electrosmart.com", 36000));
            staffManager.registerStaff(new Staff("STF002", "Jane Smith", "Store Manager", "jane@electrosmart.com", 48000));
        } catch (Exception ignored) {}
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

    private static void searchWarranty(Scanner scanner, StaffManager staffManager, ApplianceManager applianceManager) {
        System.out.println("\n--- Search Warranty Profile ---");
        System.out.print("Enter Customer ID, Appliance ID, or Serial Number: ");
        String query = scanner.nextLine().trim();
        if (query.isEmpty()) {
            System.out.println("Search query cannot be empty.");
            return;
        }

        boolean found = false;

        for (Appliance a : applianceManager.getAllAppliances()) {
            if (a.getApplianceID().equalsIgnoreCase(query) ||
               (a.hasWarranty() && a.getWarranty().getSerialNumber().equalsIgnoreCase(query)) ||
               (a.hasWarranty() && a.getWarranty().getWarrantyID().equalsIgnoreCase(query))) {
                
                System.out.println("\n[Appliance Match]");
                System.out.println("Appliance ID: " + a.getApplianceID());
                System.out.println("Model: " + a.getModelName() + " (" + a.getBrand() + ")");
                if (a.hasWarranty()) {
                    Warranty w = a.getWarranty();
                    System.out.println("Warranty ID: " + w.getWarrantyID());
                    System.out.println("Provider: " + w.getProvider());
                    System.out.println("Duration: " + w.getDurationMonths() + " months");
                    System.out.println("Extended: " + (w.isExtended() ? "Yes" : "No"));
                    if (w.getHandledBy() != null) {
                        System.out.println("Handled By Staff: " + w.getHandledBy().getName() + " (" + w.getHandledBy().getStaffID() + ")");
                    }
                } else {
                    System.out.println("Warranty: No active warranty registered.");
                }
                found = true;
            }
        }

        List<Transaction> transactions = staffManager.getAllTransactions();
        for (Transaction t : transactions) {
            if (t.getCustomerID().equalsIgnoreCase(query)) {
                System.out.println("\n[Customer Transaction Match]");
                System.out.println("Customer ID: " + t.getCustomerID());
                System.out.println("Appliance ID: " + t.getApplianceID() + " (Qty: " + t.getQuantity() + ")");
                System.out.println("Total Charged: RM" + String.format("%.2f", t.getFinalPrice()));
                System.out.println("Handled By Staff: " + t.getSoldBy().getName() + " (" + t.getSoldBy().getStaffID() + ")");
                System.out.println("Date: " + t.getSaleDate());
                
                Appliance a = applianceManager.findApplianceByID(t.getApplianceID());
                if (a != null && a.hasWarranty()) {
                    Warranty w = a.getWarranty();
                    System.out.println("Associated Warranty ID: " + w.getWarrantyID() + " (" + w.getDurationMonths() + " months)");
                }
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching warranty or transaction profile found for query: " + query);
        }
    }

    private static void generateSalesReport(StaffManager staffManager) {
        System.out.println("\n=== ElectroSmart Overall Sales Report ===");
        List<Transaction> allTransactions = staffManager.getAllTransactions();
        if (allTransactions.isEmpty()) {
            System.out.println("No sales transactions have been recorded yet.");
            return;
        }

        double totalRevenue = 0;
        int totalItemsSold = 0;
        for (Transaction t : allTransactions) {
            totalRevenue += t.getFinalPrice();
            totalItemsSold += t.getQuantity();
        }

        System.out.printf("Total Transactions: %d%n", allTransactions.size());
        System.out.printf("Total Units Sold:   %d%n", totalItemsSold);
        System.out.printf("Total Revenue:      RM%.2f%n", totalRevenue);

        System.out.println("\n--- Staff Performance Breakdown ---");
        List<Staff> staffList = staffManager.getAllStaff();
        if (staffList.isEmpty()) {
            System.out.println("No staff members registered.");
        } else {
            for (Staff s : staffList) {
                List<Transaction> staffSales = staffManager.getSalesByStaff(s.getStaffID());
                double staffRevenue = 0;
                int staffUnits = 0;
                for (Transaction t : staffSales) {
                    staffRevenue += t.getFinalPrice();
                    staffUnits += t.getQuantity();
                }
                System.out.printf("[%s] %s (%s)%n", s.getStaffID(), s.getName(), s.getRole());
                System.out.printf("  Monthly Salary: RM%.2f | Sales: %d transaction(s), %d unit(s) | Total Generated: RM%.2f%n",
                        s.getMonthlySalary(), staffSales.size(), staffUnits, staffRevenue);
            }
        }
    }

    private static void manageData(Scanner scanner, CustomerManager customerManager, StaffManager staffManager,
                                   ApplianceManager applianceManager, AccountManager accountManager) {
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
                    viewAllData(customerManager, staffManager, applianceManager, accountManager);
                    pause(scanner);
                    break;
                case "2":
                    editCustomer(scanner, customerManager);
                    pause(scanner);
                    break;
                case "3":
                    editStaff(scanner, staffManager);
                    pause(scanner);
                    break;
                case "4":
                    editAppliance(scanner, applianceManager);
                    pause(scanner);
                    break;
                case "5":
                    customerManager.clearCustomers();
                    System.out.println("All customers cleared.");
                    pause(scanner);
                    break;
                case "6":
                    staffManager.clearStaff();
                    System.out.println("All staff members cleared.");
                    pause(scanner);
                    break;
                case "7":
                    applianceManager.clearInventory();
                    System.out.println("All appliances (inventory) cleared.");
                    pause(scanner);
                    break;
                case "8":
                    staffManager.clearTransactions();
                    System.out.println("All sales transactions cleared.");
                    pause(scanner);
                    break;
                case "9":
                    accountManager.clearAccounts();
                    System.out.println("All login accounts cleared.");
                    pause(scanner);
                    break;
                case "0":
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option, please try again.");
                    pause(scanner);
            }
        }
    }

    private static void viewAllData(CustomerManager customerManager, StaffManager staffManager,
                                    ApplianceManager applianceManager, AccountManager accountManager) {
        System.out.println("\n=== ALL SYSTEM DATA ===");

        System.out.println("\n--- User Accounts ---");
        List<Account> accounts = accountManager.getAllAccounts();
        if (accounts.isEmpty()) System.out.println("No accounts.");
        else accounts.forEach(System.out::println);

        System.out.println("\n--- Staff Members ---");
        List<Staff> staff = staffManager.getAllStaff();
        if (staff.isEmpty()) System.out.println("No staff members.");
        else staff.forEach(System.out::println);

        System.out.println("\n--- Customers ---");
        List<Customer> customers = customerManager.getAllCustomers();
        if (customers.isEmpty()) System.out.println("No customers.");
        else customers.forEach(System.out::println);

        System.out.println("\n--- Appliances ---");
        List<Appliance> appliances = applianceManager.getAllAppliances();
        if (appliances.isEmpty()) System.out.println("No appliances.");
        else appliances.forEach(System.out::println);

        System.out.println("\n--- Transactions ---");
        List<Transaction> transactions = staffManager.getAllTransactions();
        if (transactions.isEmpty()) System.out.println("No transactions.");
        else transactions.forEach(System.out::println);
    }

    private static void editCustomer(Scanner scanner, CustomerManager customerManager) {
        System.out.print("\nEnter Customer ID to edit: ");
        String id = scanner.nextLine().trim();
        Customer customer = customerManager.findByID(id);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }
        System.out.println("Current details: " + customer);
        System.out.print("New Name (leave blank to keep unchanged): ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            try { customer.setName(newName); } catch (IllegalArgumentException e) { System.out.println("Error: " + e.getMessage()); }
        }
        System.out.print("New Email (leave blank to keep unchanged): ");
        String newEmail = scanner.nextLine().trim();
        if (!newEmail.isEmpty()) {
            try { customer.setEmail(newEmail); } catch (IllegalArgumentException e) { System.out.println("Error: " + e.getMessage()); }
        }
        System.out.print("New Membership Status (REGULAR/SILVER/GOLD, leave blank to keep unchanged): ");
        String statusStr = scanner.nextLine().trim();
        if (!statusStr.isEmpty()) {
            try {
                customer.setMembershipStatus(MembershipStatus.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid membership status.");
            }
        }
        System.out.println("Updated Customer: " + customer);
    }

    private static void editStaff(Scanner scanner, StaffManager staffManager) {
        System.out.print("\nEnter Staff ID to edit: ");
        String id = scanner.nextLine().trim();
        Staff staff = staffManager.findByID(id);
        if (staff == null) {
            System.out.println("Staff not found.");
            return;
        }
        System.out.println("Current details: " + staff);
        System.out.print("New Name (leave blank to keep unchanged): ");
        String newName = scanner.nextLine().trim();
        if (!newName.isEmpty()) {
            try { staff.setName(newName); } catch (IllegalArgumentException e) { System.out.println("Error: " + e.getMessage()); }
        }
        System.out.print("New Role (leave blank to keep unchanged): ");
        String newRole = scanner.nextLine().trim();
        if (!newRole.isEmpty()) {
            try { staff.setRole(newRole); } catch (IllegalArgumentException e) { System.out.println("Error: " + e.getMessage()); }
        }
        System.out.print("New Email (leave blank to keep unchanged): ");
        String newEmail = scanner.nextLine().trim();
        if (!newEmail.isEmpty()) {
            try { staff.setEmail(newEmail); } catch (IllegalArgumentException e) { System.out.println("Error: " + e.getMessage()); }
        }
        System.out.print("New Annual Salary (leave blank to keep unchanged): ");
        String salaryStr = scanner.nextLine().trim();
        if (!salaryStr.isEmpty()) {
            try {
                double salary = Double.parseDouble(salaryStr);
                staff.setAnnualSalary(salary);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        System.out.println("Updated Staff: " + staff);
    }

    private static void editAppliance(Scanner scanner, ApplianceManager applianceManager) {
        System.out.print("\nEnter Appliance ID to edit: ");
        String id = scanner.nextLine().trim();
        Appliance appliance = applianceManager.findApplianceByID(id);
        if (appliance == null) {
            System.out.println("Appliance not found.");
            return;
        }
        System.out.println("Current details: " + appliance);
        System.out.print("New Model Name (leave blank to keep unchanged): ");
        String newModel = scanner.nextLine().trim();
        if (!newModel.isEmpty()) appliance.setModelName(newModel);

        System.out.print("New Brand (leave blank to keep unchanged): ");
        String newBrand = scanner.nextLine().trim();
        if (!newBrand.isEmpty()) appliance.setBrand(newBrand);

        System.out.print("New Base Price (leave blank to keep unchanged): ");
        String priceStr = scanner.nextLine().trim();
        if (!priceStr.isEmpty()) {
            try {
                double price = Double.parseDouble(priceStr);
                if (price >= 0) appliance.setBasePrice(price);
                else System.out.println("Price cannot be negative.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format.");
            }
        }

        System.out.print("New Stock Quantity (leave blank to keep unchanged): ");
        String stockStr = scanner.nextLine().trim();
        if (!stockStr.isEmpty()) {
            try {
                int stock = Integer.parseInt(stockStr);
                if (stock >= 0) appliance.setStockQuantity(stock);
                else System.out.println("Stock cannot be negative.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format.");
            }
        }

        if (appliance instanceof WhiteGoods wg) {
            System.out.print("New Energy Rating (leave blank to keep unchanged): ");
            String er = scanner.nextLine().trim();
            if (!er.isEmpty()) wg.setEnergyRating(er);

            System.out.print("New Dimension (leave blank to keep unchanged): ");
            String dim = scanner.nextLine().trim();
            if (!dim.isEmpty()) wg.setDimension(dim);
        } else if (appliance instanceof DigitalGadgets dg) {
            System.out.print("New Operating System (leave blank to keep unchanged): ");
            String os = scanner.nextLine().trim();
            if (!os.isEmpty()) dg.setOperatingSystem(os);

            System.out.print("New Power Consumption (leave blank to keep unchanged): ");
            String pcStr = scanner.nextLine().trim();
            if (!pcStr.isEmpty()) {
                try {
                    double pc = Double.parseDouble(pcStr);
                    dg.setPowerConsumption(pc);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format.");
                }
            }
        }
        System.out.println("Updated Appliance: " + appliance);
    }
}
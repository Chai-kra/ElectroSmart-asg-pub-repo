import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
public class StaffManager {
    private List<Staff> staffList = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private int nextStaffNumber = 1;
    // [Q&A #1] Same generate-and-check ID pattern as ApplianceManager /
    // CustomerManager — see ApplianceManager.generateNextApplianceID() for
    // the full rationale.
    public String generateNextStaffID() {
        String id;
        do {
            id = String.format("STF%03d", nextStaffNumber++);
        } while (findByID(id) != null);
        return id;
    }
    // [Q&A #18] "What stops two staff members from being registered with the
    // same ID?" Explicit findByID() check below before adding — a defensive
    // backstop beyond auto-generated IDs, so even a manually-constructed
    // Staff passed in some other way can't silently overwrite an existing record.
    public void registerStaff(Staff staff) throws DuplicateStaffException {
        if (findByID(staff.getStaffID()) != null) {
            throw new DuplicateStaffException("Staff ID " + staff.getStaffID() + " already exists.");
        }
        staffList.add(staff);
    }
    private static final String ROLE_MANAGER = "Manager";
    private static final String ROLE_STAFF = "Staff";
    // [Q&A #13] Same InputValidator-consistency gap noted in Driver.java —
    // this class keeps its own private readStaffRole/readValidEmail instead
    // of routing everything through InputValidator.
    private String readStaffRole(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equals(ROLE_MANAGER) || input.equals(ROLE_STAFF)) {
                return input;
            }
            System.out.println("Invalid role — must be exactly \"Manager\" or \"Staff\" (case-sensitive).");
        }
    }
    private String readValidEmail(Scanner scanner, String prompt) {
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
    public void registerStaff(Scanner scanner) {
        System.out.println("\n--- Register New Staff ---");
        String staffID = generateNextStaffID();
        System.out.println("Assigned Staff ID: " + staffID);
        String name = InputValidator.readAlphabetOnly(scanner, "Enter Name: ");
        String role = readStaffRole(scanner, "Enter Role (Manager/Staff): ");
        String email = readValidEmail(scanner, "Enter Email: ");
        double annualSalary = InputValidator.readNonNegativeDouble(scanner, "Enter Annual Salary: ");
        try {
            Staff newStaff = new Staff(staffID, name, role, email, annualSalary);
            registerStaff(newStaff);
            System.out.println("Staff registered successfully: " + newStaff);
        } catch (DuplicateStaffException | IllegalArgumentException e) {
            System.out.println("Could not register staff: " + e.getMessage());
        }
    }
    public void viewStaffSalesReport(Scanner scanner) {
        System.out.print("\nEnter Staff ID to view their sales report: ");
        String staffID = scanner.nextLine().trim();
        Staff staff = findByID(staffID);
        if (staff == null) {
            System.out.println("No staff found with ID " + staffID);
            return;
        }
        System.out.println("Sales report for " + staff);
        List<Transaction> sales = getSalesByStaff(staffID);
        if (sales.isEmpty()) {
            System.out.println("No sales recorded for this staff member yet.");
        } else {
            for (Transaction t : sales) {
                System.out.println("  " + t);
            }
        }
    }
    public void editStaffMember(Scanner scanner) {
        System.out.print("\nEnter Staff ID to edit (0 to cancel): ");
        String id = scanner.nextLine().trim();
        if (id.equals("0")) return;
        Staff staff = findByID(id);
        if (staff == null) {
            System.out.println("No staff found with ID \"" + id + "\".");
            return;
        }
        System.out.println("Editing: " + staff);
        System.out.println("1. Name");
        System.out.println("2. Role");
        System.out.println("3. Email");
        System.out.println("4. Annual Salary");
        System.out.println("5. Remove this staff member");
        System.out.println("0. Cancel");
        System.out.print("Field to edit: ");
        String field = scanner.nextLine().trim();
        try {
            switch (field) {
                case "1":
                    staff.setName(InputValidator.readAlphabetOnly(scanner, "New Name: "));
                    break;
                case "2":
                    staff.setRole(readStaffRole(scanner, "New Role (Manager/Staff): "));
                    break;
                case "3":
                    staff.setEmail(readValidEmail(scanner, "New Email: "));
                    break;
                case "4":
                    staff.setAnnualSalary(InputValidator.readNonNegativeDouble(scanner, "New Annual Salary: "));
                    break;
                case "5":
                    removeStaff(staff.getStaffID());
                    System.out.println("Staff member " + id + " removed.");
                    return;
                case "0":
                    System.out.println("Cancelled.");
                    return;
                default:
                    System.out.println("Invalid option.");
                    return;
            }
            System.out.println("Staff member updated: " + staff);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    public void displayStaffTable() {
        if (staffList.isEmpty()) {
            System.out.println("No staff members registered yet.");
            return;
        }
        System.out.printf("%-8s %-18s %-16s %-28s %-14s%n", "ID", "Name", "Role", "Email", "Monthly Salary");
        for (Staff s : staffList) {
            System.out.printf("%-8s %-18s %-16s %-28s RM%-12.2f%n",
                    s.getStaffID(), s.getName(), s.getRole(), s.getEmail(), s.getMonthlySalary());
        }
    }
    public Staff findByID(String staffID) {
        for (Staff s : staffList) {
            if (s.getStaffID().equalsIgnoreCase(staffID)) {
                return s;
            }
        }
        return null;
    }
    public List<Staff> getAllStaff() {
        return staffList;
    }
public void loadSampleData() {
    try {
        registerStaff(new Staff("STF001", "Wei Ling", "Staff", "wei.ling@electrosmart.com", 42000));
        registerStaff(new Staff("STF002", "Daniel Cruz", "Manager", "daniel.cruz@electrosmart.com", 45000));
        registerStaff(new Staff("STF003", "Nurul Huda", "Staff", "nurul.huda@electrosmart.com", 40000));
        registerStaff(new Staff("STF004", "Kevin Lim", "Staff", "kevin.lim@electrosmart.com", 43000));
        registerStaff(new Staff("STF005", "Farah Aziz", "Manager", "farah.aziz@electrosmart.com", 48000));
        nextStaffNumber = 6;
    } catch (DuplicateStaffException | IllegalArgumentException e) {
    }
}
    // [Q&A #5] "How do you prevent orphaned transactions if a staff member is
    // deleted?" Pre-deletion check below (getSalesByStaff) blocks removal if
    // any Transaction still references this staff member — an application-level
    // stand-in for a DB foreign-key constraint.
    public void removeStaff(String staffID) {
        Staff staff = findByID(staffID);
        if (staff == null) {
            throw new IllegalArgumentException("No staff found with ID " + staffID);
        }
        if (!getSalesByStaff(staffID).isEmpty()) {
            throw new IllegalStateException(
                    "Cannot remove staff " + staffID + " — they have recorded sales.");
        }
        staffList.remove(staff);
    }
    public void recordSale(Transaction transaction) {
        transactions.add(transaction);
    }
    public List<Transaction> getSalesByStaff(String staffID) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getSoldBy().getStaffID().equalsIgnoreCase(staffID)) {
                result.add(t);
            }
        }
        return result;
    }
    public List<Transaction> getSalesByCustomer(String customerID) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getCustomerID().equalsIgnoreCase(customerID)) {
                result.add(t);
            }
        }
        return result;
    }
    public List<Transaction> getAllTransactions() {
        return transactions;
    }
    // [Q&A #16] "How do you find the top-performing staff member?" Same
    // accumulate-then-scan pattern as printSalesReport() — builds a
    // staffID->revenue map via Map.merge(), then a single linear scan for the
    // max. O(n), no sorting needed since only the single maximum is required.
    public Staff getTopPerformer() {
        Map<String, Double> revenueByStaffID = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            revenueByStaffID.merge(t.getSoldBy().getStaffID(), t.getFinalPrice(), Double::sum);
        }
        String topStaffID = null;
        double topRevenue = -1;
        for (Map.Entry<String, Double> entry : revenueByStaffID.entrySet()) {
            if (entry.getValue() > topRevenue) {
                topRevenue = entry.getValue();
                topStaffID = entry.getKey();
            }
        }
        return topStaffID == null ? null : findByID(topStaffID);
    }
    // [Q&A #15] "How is the sales report broken down by staff/appliance
    // built?" LinkedHashMaps built fresh on every call via Map.merge() —
    // no running totals maintained as sales happen. LinkedHashMap specifically
    // preserves first-seen order rather than arbitrary hash order.
    public void printSalesReport() {
        if (transactions.isEmpty()) {
            System.out.println("No sales transactions recorded yet.");
            return;
        }
        double totalRevenue = 0;
        int totalUnits = 0;
        Map<String, Double> revenueByStaff = new LinkedHashMap<>();
        Map<String, Integer> unitsByStaff = new LinkedHashMap<>();
        Map<String, Double> revenueByAppliance = new LinkedHashMap<>();
        Map<String, Integer> unitsByAppliance = new LinkedHashMap<>();
        for (Transaction t : transactions) {
            totalRevenue += t.getFinalPrice();
            totalUnits += t.getQuantity();
            String staffName = t.getSoldBy().getName();
            revenueByStaff.merge(staffName, t.getFinalPrice(), Double::sum);
            unitsByStaff.merge(staffName, t.getQuantity(), Integer::sum);
            String applianceID = t.getApplianceID();
            revenueByAppliance.merge(applianceID, t.getFinalPrice(), Double::sum);
            unitsByAppliance.merge(applianceID, t.getQuantity(), Integer::sum);
        }
        System.out.println("\n=== Sales Report ===");
        System.out.println("Total Transactions: " + transactions.size());
        System.out.println("Total Units Sold: " + totalUnits);
        System.out.printf("Total Revenue: RM%.2f%n", totalRevenue);
        System.out.println("\n--- Revenue by Staff ---");
        for (String staffName : revenueByStaff.keySet()) {
            System.out.printf("%-20s %d units | RM%.2f%n",
                    staffName, unitsByStaff.get(staffName), revenueByStaff.get(staffName));
        }
        System.out.println("\n--- Revenue by Appliance (Serial No.) ---");
        for (String applianceID : revenueByAppliance.keySet()) {
            System.out.printf("%-10s %d units | RM%.2f%n",
                    applianceID, unitsByAppliance.get(applianceID), revenueByAppliance.get(applianceID));
        }
        String topAppliance = null;
        int topUnits = -1;
        for (Map.Entry<String, Integer> entry : unitsByAppliance.entrySet()) {
            if (entry.getValue() > topUnits) {
                topUnits = entry.getValue();
                topAppliance = entry.getKey();
            }
        }
        if (topAppliance != null) {
            System.out.println("\nBest-selling appliance: " + topAppliance + " (" + topUnits + " units sold)");
        }
        Staff topPerformer = getTopPerformer();
        if (topPerformer != null) {
            System.out.println("Top-performing staff member: " + topPerformer.getName());
        }
    }
}

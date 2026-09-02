import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class StaffManager {
    private List<Staff> staffList = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private int nextStaffNumber = 1;
    public String generateNextStaffID() {
        String id;
        do {
            id = String.format("STF%03d", nextStaffNumber++);
        } while (findByID(id) != null);
        return id;
    }
    public void registerStaff(Staff staff) throws DuplicateStaffException {
        if (findByID(staff.getStaffID()) != null) {
            throw new DuplicateStaffException("Staff ID " + staff.getStaffID() + " already exists.");
        }
        staffList.add(staff);
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
    /**
     * Loads a small set of sample staff members so the system's features
     * (sales, reports, etc.) can be demonstrated immediately.
     */
    public void loadSampleData() {
        try {
            registerStaff(new Staff("STF001", "Wei Ling", "Sales Associate", "wei.ling@electrosmart.com", 42000));
            registerStaff(new Staff("STF002", "Daniel Cruz", "Sales Associate", "daniel.cruz@electrosmart.com", 45000));
            nextStaffNumber = 3;
        } catch (DuplicateStaffException | IllegalArgumentException e) {
            // Sample data is known-valid; this should never happen.
        }
    }
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

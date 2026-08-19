import java.util.ArrayList;
import java.util.List;

public class StaffManager {
    private List<Staff> staffList = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();

    /** Registers a new staff member. staffID must be unique. */
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

    /** Records a completed sale against the staff member who processed it. */
    public void recordSale(Transaction transaction) {
        transactions.add(transaction);
    }

    /** Appliances sold by a given staff member, for the staff-lookup/report view. */
    public List<Transaction> getSalesByStaff(String staffID) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getSoldBy().getStaffID().equalsIgnoreCase(staffID)) {
                result.add(t);
            }
        }
        return result;
    }
}
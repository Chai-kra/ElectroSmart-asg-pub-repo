import java.util.ArrayList;
import java.util.List;

public class CustomerManager {
    private List<Customer> customers = new ArrayList<>();
    private int nextCustomerNumber = 1;

    /**
     * NEW: Auto-generates the next free Customer ID (CUS001, CUS002, ...)
     * instead of asking the user to type one. Skips over any ID already
     * in use so it never collides.
     */
    public String generateNextCustomerID() {
        String id;
        do {
            id = String.format("CUS%03d", nextCustomerNumber++);
        } while (findByID(id) != null);
        return id;
    }
    
    public void addCustomer(Customer c) throws DuplicateCustomerException {
        for (Customer existing : customers) {
            if (existing.getCustomerID().equalsIgnoreCase(c.getCustomerID())) {
                throw new DuplicateCustomerException("Customer ID " + c.getCustomerID() + " already exists.");
            }
        }
        customers.add(c);
    }

    public Customer findByID(String customerID) {
        for (Customer c : customers) {
            if (c.getCustomerID().equalsIgnoreCase(customerID)) {
                return c;
            }
        }
        return null;
    }

    public List<Customer> getAllCustomers() {
        return customers;
    }
    /**
     * Dummy data stuff
     */
    public void loadSampleData() {
        try {
            addCustomer(new Customer("CUS001", "Aisha Rahman", "aisha.rahman@example.com", MembershipStatus.GOLD));
            addCustomer(new Customer("CUS002", "Marcus Tan", "marcus.tan@example.com", MembershipStatus.SILVER));
            addCustomer(new Customer("CUS003", "Priya Nair", "priya.nair@example.com", MembershipStatus.REGULAR));
            nextCustomerNumber = 4;
        } catch (DuplicateCustomerException | IllegalArgumentException e) {
            // Sample data is known-valid; this should never happen; But hey just in case amirite
        }
    }
}

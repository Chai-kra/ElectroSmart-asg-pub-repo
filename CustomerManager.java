import java.util.ArrayList;
import java.util.List;

public class CustomerManager {
    private List<Customer> customers = new ArrayList<>();
    private int nextCustomerNumber = 1;

    /**
     * Auto-generates the next free Customer ID (CUS001, CUS002, ...)
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
     * Tallies how many registered customers fall into each MembershipStatus
     * tier. Uses a fixed-size array (indexed by MembershipStatus.ordinal())
     * rather than a List, since the number of tiers is fixed at exactly 3
     * (REGULAR/SILVER/GOLD) and will never grow or shrink at runtime — a
     * genuine case where a plain array is the right tool, not just a List
     * wrapped for its own sake.
     */
    public int[] getMembershipBreakdown() {
        int[] counts = new int[MembershipStatus.values().length];
        for (Customer c : customers) {
            counts[c.getMemberShipStatus().ordinal()]++;
        }
        return counts;
    }

    /**
     * Dummy data stuff
     */
    public void loadSampleData() {
        try {
            addCustomer(new IndividualCustomer("CUS001", "Aisha Rahman", "aisha.rahman@example.com", MembershipStatus.GOLD, "990101-14-5566"));
            addCustomer(new IndividualCustomer("CUS002", "Marcus Tan", "marcus.tan@example.com", MembershipStatus.SILVER, "880202-10-1234"));
            addCustomer(new CorporateCustomer("CUS003", "Priya Nair", "priya.nair@example.com", MembershipStatus.REGULAR, "Nair Enterprises", "Priya Nair"));
            nextCustomerNumber = 4;
        } catch (DuplicateCustomerException | IllegalArgumentException e) {
            // Sample data is known-valid; this should never happen; But hey just in case amirite
        }
    }
}
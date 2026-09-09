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
    // [Q&A #1] Same generate-and-check ID pattern as ApplianceManager /
    // StaffManager — see ApplianceManager.generateNextApplianceID() for the
    // full rationale. Public here (unlike Appliance's version) because Driver
    // calls this directly before the Customer object exists.
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
    // [Q&A #17] "Why a plain array instead of a Map<MembershipStatus,Integer>?"
    // Indexed by MembershipStatus.ordinal() — a fixed-size array is the right
    // tool since the tier count (3) is fixed at compile time and never grows
    // or shrinks at runtime, unlike a Map which would be unneeded overhead here.
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
            addCustomer(new IndividualCustomer("CUS004", "Hafiz Rosli", "hafiz.rosli@example.com", MembershipStatus.REGULAR, "920315-08-4321"));
            addCustomer(new IndividualCustomer("CUS005", "Ling Mei Fen", "ling.meifen@example.com", MembershipStatus.GOLD, "870711-14-9988"));
            addCustomer(new CorporateCustomer("CUS006", "Ganesh Kumar", "ganesh.kumar@example.com", MembershipStatus.SILVER, "Ganesh Trading Co.", "Ganesh Kumar"));
            addCustomer(new IndividualCustomer("CUS007", "Nabila Iskandar", "nabila.iskandar@example.com", MembershipStatus.SILVER, "950528-10-2211"));
            addCustomer(new IndividualCustomer("CUS008", "Thomas Wong", "thomas.wong@example.com", MembershipStatus.REGULAR, "890912-14-3344"));
            addCustomer(new CorporateCustomer("CUS009", "Suraya Ismail", "suraya.ismail@example.com", MembershipStatus.GOLD, "Suraya Holdings Sdn Bhd", "Suraya Ismail"));
            addCustomer(new IndividualCustomer("CUS010", "Vincent Lee", "vincent.lee@example.com", MembershipStatus.SILVER, "910403-08-5567"));
            addCustomer(new IndividualCustomer("CUS011", "Kavitha Selvam", "kavitha.selvam@example.com", MembershipStatus.REGULAR, "930620-10-7788"));
            addCustomer(new CorporateCustomer("CUS012", "Firdaus Halim", "firdaus.halim@example.com", MembershipStatus.SILVER, "Firdaus Electronics", "Firdaus Halim"));
            addCustomer(new IndividualCustomer("CUS013", "Grace Anand", "grace.anand@example.com", MembershipStatus.GOLD, "860825-14-6699"));
            nextCustomerNumber = 14;
        } catch (DuplicateCustomerException | IllegalArgumentException e) {
            // Sample data is known-valid; this should never happen; But hey just in case amirite
        }
    }
}

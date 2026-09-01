public class Customer {
    private String customerID;
    private String name;
    private String email;
    private MembershipStatus membershipStatus;

    public Customer(String customerID, String name, String email, MembershipStatus membershipStatus) {
        if (customerID == null || customerID.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID cannot be empty.");
        }
        if (membershipStatus == null) {
            throw new IllegalArgumentException("Membership status cannot be null.");
        }
        this.customerID = customerID;
        // CHANGED: route through the validating setters instead of assigning fields
        // directly, so the constructor can't be used to bypass the same rules the
        // setters enforce (e.g. an empty name or a malformed email).
        setName(name);
        setEmail(email);
        this.membershipStatus = membershipStatus;
    }

    public String getCustomerID() {
        return customerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        this.email = email;
    }

    public MembershipStatus getMemberShipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(MembershipStatus membershipStatus) {
        if (membershipStatus == null) {
            throw new IllegalArgumentException("Membership status cannot be null.");
        }
        this.membershipStatus = membershipStatus;
    }

    /**
     * CHANGED: delegates to MembershipStatus.getDiscountRate() instead of
     * re-implementing the same 0% / 5% / 10% mapping in a separate switch here.
     * MembershipStatus already owns that mapping — Customer just aggregates a
     * MembershipStatus and asks it for the rate, so there's one source of truth.
     */
    public double getDiscountRate() {
        return membershipStatus.getDiscountRate();
    }

    // toString to look Customer object more nicely when display
    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | Discount: %.0f%%",
                customerID, name, email, membershipStatus, getDiscountRate() * 100);
    }
}
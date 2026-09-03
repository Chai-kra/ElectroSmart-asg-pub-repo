/**
 * A Customer who is a private individual. Adds an IC (identity card)
 * number on top of the base Customer fields — demonstrates inheritance
 * from Customer, and polymorphism via getCustomerCategory() below.
 */
public class IndividualCustomer extends Customer {
    private String icNumber;

    public IndividualCustomer(String customerID, String name, String email,
                               MembershipStatus membershipStatus, String icNumber) {
        super(customerID, name, email, membershipStatus);
        setIcNumber(icNumber);
    }

    public String getIcNumber() {
        return icNumber;
    }

    public void setIcNumber(String icNumber) {
        if (icNumber == null || icNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("IC number cannot be empty.");
        }
        this.icNumber = icNumber;
    }

    @Override
    public String getCustomerCategory() {
        return "Individual";
    }

    @Override
    public String toString() {
        return super.toString() + " | IC: " + icNumber;
    }
}

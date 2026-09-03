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
        // Malaysian IC format: YYMMDD-PB-###G — dashes required, matching the format shown to the user.
        if (!icNumber.trim().matches("\\d{6}-\\d{2}-\\d{4}")) {
            throw new IllegalArgumentException("IC number must follow the format 990101-14-5566 (dashes required).");
        }
        this.icNumber = icNumber;
    }

    @Override
    public String getCustomerCategory() {
        return "Individual";
    }

    @Override
    public String getExtraInfo() {
        return "IC: " + icNumber;
    }

    @Override
    public String toString() {
        return super.toString() + " | IC: " + icNumber;
    }
}

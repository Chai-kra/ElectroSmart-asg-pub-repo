 public class Customer{
    private String customerID;
    private String name;
    private String email;
    private MembershipStatus membershipStatus;

  
    public Customer(String customerID,String name,String email,MembershipStatus membershipStatus){
        this.customerID=customerID;
        this.name=name;
        this.email=email;
        this.membershipStatus=membershipStatus;
    }

    public String getCustomerID(){
        return customerID;
    }

    public String getName(){
     return name;
    }

    public void setName(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty.");
            }
            this.name = name;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        this.email = email;
    }

    public MembershipStatus getMemberShipStatus(){
        return membershipStatus;
    }

    public void setMembershipStatus(MembershipStatus membershipStatus){
        this.membershipStatus=membershipStatus;
    }

    public double getDiscountRate() {
        switch (membershipStatus) {
            case SILVER: return 0.05;
            case GOLD:   return 0.10;
            case REGULAR:
            default:     return 0.0;
        }
  
    }

//toString to look Customer object more nicely when display
   @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | Discount: %.0f%%",
                customerID, name, email, membershipStatus, getDiscountRate() * 100);
    }
} 




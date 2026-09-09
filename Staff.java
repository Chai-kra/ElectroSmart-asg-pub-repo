public class Staff {
    private String staffID;
    private String name;
    private String role;
    private String email;
    private double annualSalary;
    public Staff(String staffID, String name, String role, String email, double annualSalary) {
        if (staffID == null || staffID.trim().isEmpty()) {
            throw new IllegalArgumentException("Staff ID cannot be empty.");
        }
        this.staffID = staffID;
        setName(name);
        setRole(role);
        setEmail(email);
        setAnnualSalary(annualSalary);
    }
    public String getStaffID() {
        return staffID;
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
    public String getRole() {
        return role;
    }
    // [Q&A #14] "Why is role a String when AccountRole/MembershipStatus/
    // ClaimStatus are all enums?" Known inconsistency — worth naming
    // proactively. An enum would move an invalid role to a compile-time
    // impossibility instead of this runtime IllegalArgumentException, and
    // would remove the ROLE_MANAGER/ROLE_STAFF constants duplicated here and
    // in StaffManager.java. Flagged as a contained future refactor, not a bug.
    private static final String ROLE_MANAGER = "Manager";
    private static final String ROLE_STAFF = "Staff";
    public void setRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be empty.");
        }
        if (!role.equals(ROLE_MANAGER) && !role.equals(ROLE_STAFF)) {
            throw new IllegalArgumentException(
                    "Invalid role \"" + role + "\". Role must be exactly \"Manager\" or \"Staff\" (case-sensitive, no extra spaces).");
        }
        this.role = role;
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
    public double getAnnualSalary() {
        return annualSalary;
    }
    public void setAnnualSalary(double annualSalary) {
        if (annualSalary < 0) {
            throw new IllegalArgumentException("Annual salary cannot be negative.");
        }
        this.annualSalary = annualSalary;
    }
    public double getMonthlySalary() {
        return annualSalary / 12;
    }
    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | Monthly Salary: %.2f",
                staffID, name, role, email, getMonthlySalary());
    }
}

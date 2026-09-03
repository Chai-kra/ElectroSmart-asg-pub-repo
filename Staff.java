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
        // model-level guard, mirroring Customer's setName() — the object
        // enforces this itself instead of trusting the caller to have checked.
        if (!name.trim().matches("[A-Za-z ]+")) {
            throw new IllegalArgumentException("Name can only contain alphabet letters.");
        }
        this.name = name;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be empty.");
        }
        if (!role.trim().matches("[A-Za-z ]+")) {
            throw new IllegalArgumentException("Role can only contain alphabet letters (e.g. Sales Associate).");
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

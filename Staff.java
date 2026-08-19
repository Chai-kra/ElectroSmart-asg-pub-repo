public class Staff {
    private String staffID;
    private String name;
    private String role;
    private String email;
    private double annualSalary;

    public Staff(String staffID, String name, String role, String email, double annualSalary) {
        this.staffID = staffID;
        this.name = name;
        this.role = role;
        this.email = email;
        this.annualSalary = annualSalary;
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

    public void setRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be empty.");
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

    /** Monthly salary is derived from annualSalary, not stored separately. */
    public double getMonthlySalary() {
        return annualSalary / 12;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | %s | %s | Monthly Salary: %.2f",
                staffID, name, role, email, getMonthlySalary());
    }
}
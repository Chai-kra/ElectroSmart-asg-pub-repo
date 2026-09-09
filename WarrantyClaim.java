/**
 * A single repair claim filed against a Warranty. Tracks the issue reported,
 * the estimated/settled repair cost, and its status as it moves through
 * settlement (PENDING -> APPROVED/REJECTED -> COMPLETED).
 */
public class WarrantyClaim {
    private String claimID;
    private String issueDescription;
    private double repairCost;
    private ClaimStatus status;
    private Staff settledBy;
    private java.util.Date filedDate;

    public WarrantyClaim(String claimID, String issueDescription, double repairCost) {
        if (claimID == null || claimID.isBlank()) {
            throw new IllegalArgumentException("Claim ID cannot be empty.");
        }
        if (issueDescription == null || issueDescription.isBlank()) {
            throw new IllegalArgumentException("Issue description cannot be empty.");
        }
        if (repairCost < 0) {
            throw new IllegalArgumentException("Repair cost cannot be negative.");
        }
        this.claimID = claimID;
        this.issueDescription = issueDescription;
        this.repairCost = repairCost;
        this.status = ClaimStatus.PENDING;
        this.filedDate = new java.util.Date();
    }

    public String getClaimID() {
        return claimID;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public double getRepairCost() {
        return repairCost;
    }

    public void setRepairCost(double repairCost) {
        if (repairCost < 0) {
            throw new IllegalArgumentException("Repair cost cannot be negative.");
        }
        this.repairCost = repairCost;
    }

    public ClaimStatus getStatus() {
        return status;
    }

    /** The staff member who settled (approved/rejected/completed) this claim. */
    public Staff getSettledBy() {
        return settledBy;
    }

    public java.util.Date getFiledDate() {
        return filedDate;
    }

    /**
     * Moves this claim to a new status and records which staff member
     * settled it, so settlement is always accountable to a staff member.
     */
    public void setStatus(ClaimStatus status, Staff staff) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
        }
        if (staff == null) {
            throw new IllegalArgumentException("A staff member must be supplied to settle a claim.");
        }
        this.status = status;
        this.settledBy = staff;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Repair Cost: RM%.2f | Status: %s%s",
                claimID, issueDescription, repairCost, status,
                settledBy != null ? " | Settled by: " + settledBy.getName() : "");
    }
}

public class Warranty {

    private String warrantyID;
    private String serialNumber;
    private String provider;
    private int durationMonths;
    private boolean isExtended;
    private Staff handledBy;

    public Warranty(String warrantyID, String serialNumber, String provider, int durationMonths) {
    this.warrantyID = warrantyID;
    this.serialNumber = serialNumber;
    this.provider = provider;
    this.durationMonths = durationMonths;
    this.isExtended = false; //a new warranty is not extended by default
    }

    public String getWarrantyID() {
        return warrantyID;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getProvider() {
        return provider;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public boolean isExtended() {
        return isExtended;
    }

    /** The staff member who activated or most recently extended this warranty. -- Chai*/
    public Staff getHandledBy() {
        return handledBy;
    }

    public void extendWarranty(int extraMonths, Staff staff)
        throws InvalidWarrantyExtensionException {

    if (staff == null) { /** Staff requirement for extending waranty*/
        throw new InvalidWarrantyExtensionException(
                "A staff member must be supplied to extend a warranty."
        );
    }

    if (extraMonths <= 0) {
        throw new InvalidWarrantyExtensionException(
                "Extension months must be greater than 0."
        );
    }

    if (durationMonths + extraMonths > 60) {
        throw new InvalidWarrantyExtensionException(
                "Warranty period cannot exceed 60 months."
        ); // Assuming 60 months is the maximum allowed warranty period
    }

    durationMonths = durationMonths + extraMonths;
    isExtended = true;
    handledBy = staff;
    }

    public void activate(Staff staff) {
    if (staff == null) {
        throw new IllegalArgumentException("A staff member must be supplied to activate a warranty.");
    }
    handledBy = staff;
    System.out.println("Warranty " + warrantyID + " is activated by " + staff.getName() + ".");
    }

}
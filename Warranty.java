public class Warranty {

    private String warrantyID;
    private String serialNumber;
    private String provider;
    private int durationMonths;
    private boolean isExtended;

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

    public void extendWarranty(int extraMonths)
        throws InvalidWarrantyExtensionException {

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
    }
    
    public void activate() {
    System.out.println("Warranty " + warrantyID + " is activated.");
    }




}

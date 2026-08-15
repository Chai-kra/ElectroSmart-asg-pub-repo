public abstract class Appliance implements WarrantyEligible {

    private String applianceID;
    private String modelName;
    private String brand;
    private double basePrice;
    private int stockQuantity;
    private Warranty warranty;

    public Appliance(String applianceID, String modelName, String brand,
                     double basePrice, int stockQuantity) {

        this.applianceID = applianceID;
        this.modelName = modelName;
        this.brand = brand;
        this.basePrice = basePrice;
        this.stockQuantity = stockQuantity;
    }

    public String getApplianceID() { return applianceID; }
    public String getModelName() { return modelName; }
    public String getBrand() { return brand; }
    public double getBasePrice() { return basePrice; }
    public int getStockQuantity() { return stockQuantity; }

    public Warranty getWarranty() {
        return warranty;
    }

    public void setWarranty(Warranty warranty) {
        this.warranty = warranty;
    }

    @Override
    public void activateWarranty() {
    if (warranty != null) {
        warranty.activate();
        }
    }
    
    @Override
    public void extendWarranty(int extraMonths) {
    if (warranty != null) {
        try {
            warranty.extendWarranty(extraMonths);
            } catch (InvalidWarrantyExtensionException e) {
            System.out.println(e.getMessage());
            }
        }
    }

    public double calculateFinalPrice() {
        return basePrice;
    }
}
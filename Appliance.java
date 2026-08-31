public abstract class Appliance implements WarrantyEligible {
    private String applianceID;
    private String modelName;
    private String brand;
    private double basePrice;
    private int stockQuantity;
    private Warranty warranty;

    public Appliance(String applianceID, String modelName, String brand, double basePrice, int stockQuantity) {
        if (applianceID == null || applianceID.isBlank()) {
            throw new IllegalArgumentException("Appliance ID cannot be empty.");
        }
        if (basePrice < 0) {
            throw new IllegalArgumentException("Base price cannot be negative.");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative.");
        }
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
    public Warranty getWarranty() { return warranty; }

    public void setModelName(String modelName) { this.modelName = modelName; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }

    public void reduceStock(int qty) { this.stockQuantity -= qty; }
    public boolean isLowStock() { return this.stockQuantity < 3; }
    public boolean hasWarranty() { return this.warranty != null; }
    
    public Warranty getWarranty() {
        return warranty;
    }

    public abstract double calculateFinalPrice();
    protected abstract String getDefaultProvider();
    protected abstract int getDefaultDuration();

    @Override
    public void activateWarranty(Staff staff) {
        this.warranty = new Warranty(
            "W-" + this.applianceID,
            this.applianceID,
            getDefaultProvider(),
            getDefaultDuration()
        );
        warranty.activate(staff);
    }

    @Override
    public void extendWarranty(int extraMonths, Staff staff) throws InvalidWarrantyExtensionException {
        if (this.warranty == null) {
            throw new InvalidWarrantyExtensionException("No warranty has been activated for this appliance yet.");
        }
        this.warranty.extendWarranty(extraMonths, staff);
    }

    @Override
    public String toString() {
        return modelName + " (" + brand + ") - RM" + String.format("%.2f", calculateFinalPrice()) + " | Stock " + stockQuantity;
    }
}
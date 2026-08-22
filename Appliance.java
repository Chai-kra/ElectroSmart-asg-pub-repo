public abstract class Appliance implements WarrantyEligible {    //zq
    // encapsulation (private field)
    private String applianceID;
    private String modelName;
    private String brand;
    private double basePrice;
    private int stockQuantity;
    private Warranty warranty;

    // constructor (when create a new Appliance)
    public Appliance(String applianceID, String modelName, String brand, double basePrice, int stockQuantity) {
        // validation
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

    // getters
    public String getApplianceID() {
        return applianceID;
    }
    public String getModelName() {
        return modelName;
    }
    public String getBrand() {
        return brand;
    }
    public double getBasePrice() {
        return basePrice;
    }
    public int getStockQuantity() {
        return stockQuantity;
    }

    // setter
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void reduceStock(int qty) {
        this.stockQuantity = this.stockQuantity - qty;
    }

    public boolean isLowStock() {
        return this.stockQuantity < 3;
    }

    // each subclass MUST provide its own version (overriden by subclasses)
    public abstract double calculateFinalPrice();

    protected abstract String getDefaultProvider();
    protected abstract int getDefaultDuration();

    @Override
    public void activateWarranty(Staff staff) {
        this.warranty = new Warranty(
            "W-" + this.applianceID,   // simple warrantyID scheme, adjust as your team prefers
            this.applianceID,          // using applianceID as the "serial number" link
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
        return modelName + " (" + brand + ") - RM" + calculateFinalPrice() + " | Stock " + stockQuantity;
    }
}
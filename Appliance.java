public abstract class Appliance {
    private String applianceID;
    private String modelName;
    private String brand;
    private double basePrice;
    private int stockQuantity;

    public Appliance(String applianceID, String modelName, String brand, double basePrice, int stockQuantity) {
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

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void reduceStock(int qty) {
        this.stockQuantity = this.stockQuantity - qty;
    }

    public boolean isLowStock() {
        return this.stockQuantity < 3;
    }

    // no body here — each subclass MUST provide its own version
    public abstract double calculateFinalPrice();
}
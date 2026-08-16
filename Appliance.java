public abstract class Appliance {    //zq
    // encapsulation (private field)
    private String applianceID;
    private String modelName;
    private String brand;
    private double basePrice;
    private int stockQuantity;

    // constructor (when create a new Appliance)
    public Appliance(String applianceID, String modelName, String brand, double basePrice, int stockQuantity) {
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
    public String getBasePrice() {
        return basePrice;
    }
    public String getStockQuantity() {
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
}
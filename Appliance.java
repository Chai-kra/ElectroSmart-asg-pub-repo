public class Appliance {
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
    public String getmodelName() {
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

    // method every appliance will use (overrriden by subclasses)
    public double calculateFinalPrice() {
        return basePrice;
    }
}
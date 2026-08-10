public class Appliance {
    // Step A: private fields (encapsulation — nobody outside this class can touch these directly)
    private String applianceID;
    private String modelName;
    private String brand;
    private double basePrice;
    private int stockQuantity;

    // Step B: constructor — runs when you create a new Appliance
    public Appliance(String applianceID, String modelName, String brand, double basePrice, int stockQuantity) {
        this.applianceID = applianceID;
        this.modelName = modelName;
        this.brand = brand;
        this.basePrice = basePrice;
        this.stockQuantity = stockQuantity;
    }

    // Step C: getters — how outside code reads private fields
    public String getApplianceID() { return applianceID; }
    public String getModelName() { return modelName; }
    public String getBrand() { return brand; }
    public double getBasePrice() { return basePrice; }
    public int getStockQuantity() { return stockQuantity; }

    // Step D: a method every appliance will use
    public double calculateFinalPrice() {
        return basePrice; // base version — subclasses will override this
    }
}
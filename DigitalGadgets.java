public class DigitalGadgets extends Appliance {
    private String operatingSystem;
    private double powerConsumption;
    private static final double RECYCLING_LEVY_RATE = 0.02;

    public DigitalGadgets(String applianceID, String modelName, String brand, double basePrice, int stockQuantity,
                           String operatingSystem, double powerConsumption) {
        super(applianceID, modelName, brand, basePrice, stockQuantity);
        this.operatingSystem = operatingSystem;
        this.powerConsumption = powerConsumption;
    }

    public String getOperatingSystem() { return operatingSystem; }
    public double getPowerConsumption() { return powerConsumption; }

    public void setOperatingSystem(String operatingSystem) {
        if (operatingSystem == null || operatingSystem.isBlank()) {
            throw new IllegalArgumentException("Operating system cannot be empty.");
        }
        this.operatingSystem = operatingSystem;
    }

    public void setPowerConsumption(double powerConsumption) {
        if (powerConsumption < 0) {
            throw new IllegalArgumentException("Power consumption cannot be negative.");
        }
        this.powerConsumption = powerConsumption;
    }

    @Override
    public double calculateFinalPrice() { return getBasePrice() + (getBasePrice() * RECYCLING_LEVY_RATE); }
    @Override
    protected String getDefaultProvider() { return "Store"; }
    @Override
    protected int getDefaultDuration() { return 12; }
}
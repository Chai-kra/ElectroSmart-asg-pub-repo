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

    @Override
    public double calculateFinalPrice() { return getBasePrice() + (getBasePrice() * RECYCLING_LEVY_RATE); }

    @Override
    protected String getDefaultProvider() { return "Store"; }

    @Override
    protected int getDefaultDuration() { return 12; }
}
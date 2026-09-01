public class WhiteGoods extends Appliance {
    private String energyRating;
    private String dimension;
    private static final double DELIVERY_SURCHARGE = 50.0;

    public WhiteGoods(String applianceID, String modelName, String brand, double basePrice, int stockQuantity,
                       String energyRating, String dimension) {
        super(applianceID, modelName, brand, basePrice, stockQuantity);
        this.energyRating = energyRating;
        this.dimension = dimension;
    }

    public String getEnergyRating() { return energyRating; }
    public String getDimension() { return dimension; }

    public void setEnergyRating(String energyRating) {
        if (energyRating == null || energyRating.isBlank()) {
            throw new IllegalArgumentException("Energy rating cannot be empty.");
        }
        this.energyRating = energyRating;
    }

    public void setDimension(String dimension) {
        if (dimension == null || dimension.isBlank()) {
            throw new IllegalArgumentException("Dimension cannot be empty.");
        }
        this.dimension = dimension;
    }

    @Override
    public double calculateFinalPrice() { return getBasePrice() + DELIVERY_SURCHARGE; }
    @Override
    protected String getDefaultProvider() { return "Manufacturer"; }
    @Override
    protected int getDefaultDuration() { return 24; }
    @Override
    public String getSurchargeLabel() { return "Delivery Surcharge"; }
}
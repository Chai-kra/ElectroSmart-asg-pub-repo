public class WhiteGoods extends Appliance { //zq
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

    @Override   // replacing the parent's version
    public double calculateFinalPrice() {
        return getBasePrice() + DELIVERY_SURCHARGE;
    }

    @Override
        protected String getDefaultProvider() {
            return "Manufacturer";
        }

        @Override
        protected int getDefaultDuration() {
            return 24;
        }
}
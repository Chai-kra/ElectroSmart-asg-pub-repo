public enum MembershipStatus {
    REGULAR(0.0),
    SILVER(0.05),
    GOLD(0.10);

    private final double discountRate;

    MembershipStatus(double discountRate) {
        this.discountRate = discountRate;
    }

    /**
     * Returns the discount rate for this membership status.
     * @return discount rate as a decimal (e.g., 0.05 for 5%)
     */
    public double getDiscountRate() {
        return discountRate;
    }
}
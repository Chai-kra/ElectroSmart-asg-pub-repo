public enum MembershipStatus {
    REGULAR(0.0),
    SILVER(0.05),
    GOLD(0.10);

    private final double discountRate;

    MembershipStatus(double discountRate) {
        this.discountRate = discountRate;
    }

    // [Q&A #8] "How is a customer's discount rate calculated?" This enum
    // owns the tier -> rate mapping as constructor state, not a switch
    // statement elsewhere — see Customer.getDiscountRate(), which just
    // delegates to this method. One source of truth for what each tier means.
    /**
     * Returns the discount rate for this membership status.
     * @return discount rate as a decimal (e.g., 0.05 for 5%)
     */
    public double getDiscountRate() {
        return discountRate;
    }
}

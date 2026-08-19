/**
 * A record of sale,this class only has fields the
 * Staff module needs for accountability and reporting of staff sale.
 */
public class Transaction {
    private String applianceID;
    private String customerID;
    private int quantity;
    private double finalPrice;
    private Staff soldBy;
    private java.util.Date saleDate;

    public Transaction(String applianceID, String customerID, int quantity,
            double finalPrice, Staff soldBy) {
        if (soldBy == null) {
            throw new IllegalArgumentException("A staff member must be supplied for soldBy.");
        }
        this.applianceID = applianceID;
        this.customerID = customerID;
        this.quantity = quantity;
        this.finalPrice = finalPrice;
        this.soldBy = soldBy;
        this.saleDate = new java.util.Date();
    }

    public String getApplianceID() {
        return applianceID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getFinalPrice() {
        return finalPrice;
    }

    public Staff getSoldBy() {
        return soldBy;
    }

    public java.util.Date getSaleDate() {
        return saleDate;
    }

    @Override
    public String toString() {
        return "Appliance " + applianceID + " x" + quantity + " sold to Customer " + customerID +
                " for " + finalPrice + " by " + soldBy.getName() + " on " + saleDate;
    }
}
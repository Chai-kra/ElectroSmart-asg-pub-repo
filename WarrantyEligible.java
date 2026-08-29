public interface WarrantyEligible {
    void activateWarranty(Staff staff);
    void extendWarranty(int extraMonths, Staff staff) throws InvalidWarrantyExtensionException;
}
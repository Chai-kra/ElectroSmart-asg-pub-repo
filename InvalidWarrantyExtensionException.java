// [Q&A #9] "Why custom checked exceptions instead of generic RuntimeException?"
// Represents an expected business-rule violation (not a bug) — checked so
// every call site is forced to handle it explicitly via catch. See
// StaffManager.registerStaff() / Warranty.extendWarranty() for throw sites.
public class InvalidWarrantyExtensionException extends Exception {
    public InvalidWarrantyExtensionException(String message) {
        super(message);
    }
}

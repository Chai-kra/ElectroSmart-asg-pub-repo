import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Handles everything downstream of the inventory itself: processing a sale
 * (stock reduction, member pricing, warranty activation), extending an
 * existing warranty, and filing/settling repair claims. Reads appliances
 * through the ApplianceManager it's given rather than keeping its own copy
 * of the inventory, so there's exactly one source of truth for stock and
 * exactly one class responsible for warranty/claims behaviour.
 */
public class WarrantyManager {
    private final ApplianceManager applianceManager;

    public WarrantyManager(ApplianceManager applianceManager) {
        this.applianceManager = applianceManager;
    }

    public void processSale(Scanner scanner, CustomerManager customerManager, StaffManager staffManager, Staff currentStaff)
            throws InvalidWarrantyExtensionException {
        System.out.println("\n=== Available Appliances ===");
        if (applianceManager.getAllAppliances().isEmpty()) {
            System.out.println("No appliances in inventory yet.");
            return;
        }
        applianceManager.displayInventory();

        Customer customer = null;
        while (customer == null) {
            System.out.print("\nEnter Customer ID (0 to cancel): ");
            String customerID = scanner.nextLine().trim();
            if (customerID.equals("0")) { System.out.println("Sale cancelled."); return; }
            customer = customerManager.findByID(customerID);
            if (customer == null) {
                System.out.println("No customer found with ID \"" + customerID + "\". Register them first, or try again.");
            }
        }

        Staff staff = null;
        while (staff == null) {
            System.out.print("Enter Staff ID handling sale (0 to cancel): ");
            String staffID = scanner.nextLine().trim();
            if (staffID.equals("0")) { System.out.println("Sale cancelled."); return; }
            staff = staffManager.findByID(staffID);
            if (staff == null) {
                System.out.println("No staff found with ID \"" + staffID + "\". Register them first, or try again.");
            }
        }

        List<Appliance> cartAppliances = new ArrayList<>();
        List<Integer> cartQuantities = new ArrayList<>();
        double runningTotal = 0;
        boolean addingItems = true;

        while (addingItems) {
            Appliance appliance = null;
            while (appliance == null) {
                System.out.print("\nEnter Appliance ID (0 to stop adding items): ");
                String applianceID = scanner.nextLine().trim();
                if (applianceID.equals("0")) break;
                appliance = applianceManager.findApplianceByID(applianceID);
                if (appliance == null) {
                    System.out.println("No appliance found with ID \"" + applianceID + "\". Please try again.");
                }
            }
            if (appliance == null) break;

            // an item with 0 stock can never satisfy a quantity request, so don't
            // trap the user in an unanswerable "Quantity for..." loop — bounce them
            // straight back to picking a different appliance.
            if (appliance.getStockQuantity() == 0) {
                System.out.println("\"" + appliance.getModelName() + "\" is currently out of stock (0 available). Please choose a different appliance.");
                continue;
            }

            int quantity = 0;
            boolean itemCancelled = false;
            while (true) {
                System.out.print("Quantity for " + appliance.getModelName() + " (Available: " + appliance.getStockQuantity() + ", 0 to cancel this item): ");
                String input = scanner.nextLine().trim();
                try {
                    quantity = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number — please enter a whole number.");
                    continue;
                }
                if (quantity == 0) { itemCancelled = true; break; }
                if (quantity < 0) { System.out.println("Quantity must be at least 1."); continue; }
                if (quantity > appliance.getStockQuantity()) {
                    System.out.println("Not enough stock — only " + appliance.getStockQuantity() + " available.");
                    continue;
                }
                break;
            }

            if (itemCancelled) {
                System.out.println("Item cancelled.");
                continue;
            }

            cartAppliances.add(appliance);
            cartQuantities.add(quantity);
            double lineTotal = appliance.calculateFinalPrice() * quantity;
            runningTotal += lineTotal;
            System.out.printf("Added: %d x %s (Base RM%.2f + %s RM%.2f = RM%.2f each, RM%.2f total)%n",
                    quantity, appliance.getModelName(), appliance.getBasePrice(),
                    appliance.getSurchargeLabel(), appliance.getSurchargeAmount(),
                    appliance.calculateFinalPrice(), lineTotal);
            if (!InputValidator.readYesNo(scanner, "Add another item? (Y/N): ")) addingItems = false;
        }

        if (cartAppliances.isEmpty()) {
            System.out.println("No items were added. Sale cancelled.");
            return;
        }

        double discount = customer.getDiscountRate();
        double finalTotal = runningTotal * (1 - discount);
        System.out.println("\n=== Order Summary ===");
        for (int i = 0; i < cartAppliances.size(); i++) {
            Appliance a = cartAppliances.get(i);
            int qty = cartQuantities.get(i);
            // shows the base price + surcharge/levy breakdown per unit, so it's
            // clear the WhiteGoods delivery surcharge / DigitalGadgets recycling
            // levy is applied.
            System.out.printf("%d x %-20s Base RM%.2f + %s RM%.2f = RM%.2f each -> RM%.2f%n",
                    qty, a.getModelName(), a.getBasePrice(), a.getSurchargeLabel(),
                    a.getSurchargeAmount(), a.calculateFinalPrice(), a.calculateFinalPrice() * qty);
        }
        System.out.printf("Subtotal: RM%.2f%n", runningTotal);
        System.out.printf("Membership Discount (%s): -%.0f%%%n", customer.getMemberShipStatus(), discount * 100);
        System.out.printf("Total: RM%.2f%n", finalTotal);
        if (!InputValidator.readYesNo(scanner, "Confirm sale? (Y/N): ")) {
            System.out.println("Sale cancelled.");
            return;
        }

        for (int i = 0; i < cartAppliances.size(); i++) {
            Appliance a = cartAppliances.get(i);
            int qty = cartQuantities.get(i);
            double lineTotal = a.calculateFinalPrice() * qty * (1 - discount);
            a.reduceStock(qty);
            a.activateWarranty(staff);
            staffManager.recordSale(new Transaction(a.getApplianceID(), customer.getCustomerID(), qty, lineTotal, staff));
        }

        System.out.println("\n=== Sale Complete ===");
        System.out.println("Customer: " + customer.getName());
        System.out.println("Staff: " + staff.getName());
        System.out.printf("Total charged: RM%.2f%n", finalTotal);
    }

    public void extendApplianceWarranty(Scanner scanner, Staff currentStaff)
            throws InvalidWarrantyExtensionException {
        Appliance appliance = selectApplianceWithWarranty(scanner);
        if (appliance == null) return;

        Staff staff = currentStaff;

        int extraMonths = 0;
        while (true) {
            System.out.print("Months to extend by: ");
            String input = scanner.nextLine().trim();
            try {
                extraMonths = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number — please enter a whole number.");
                continue;
            }
            if (extraMonths <= 0) { System.out.println("Must be at least 1 month."); continue; }
            break;
        }

        appliance.extendWarranty(extraMonths, staff);
        System.out.println("Warranty extended successfully by " + extraMonths + " months by staff member " + staff.getName() + ".");
    }

    /** Files a repair claim against an appliance's active warranty. */
    public void fileWarrantyClaim(Scanner scanner, Staff currentStaff) {
        Appliance appliance = selectApplianceWithWarranty(scanner);
        if (appliance == null) return;

        Warranty warranty = appliance.getWarranty();

        String issue = null;
        while (issue == null) {
            System.out.println("\nWhat's the issue?");
            System.out.println("1. Not powering");
            System.out.println("2. Physical damage");
            System.out.println("3. Malfunctioning");
            System.out.println("4. Other");
            System.out.print("Select an option: ");
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": issue = "Not powering"; break;
                case "2": issue = "Physical damage"; break;
                case "3": issue = "Malfunctioning"; break;
                case "4":
                    while (true) {
                        System.out.print("Please describe the issue: ");
                        String custom = scanner.nextLine().trim();
                        if (custom.isEmpty()) {
                            System.out.println("Issue description cannot be empty.");
                            continue;
                        }
                        issue = custom;
                        break;
                    }
                    break;
                default:
                    System.out.println("Invalid option — please enter 1, 2, 3, or 4.");
            }
        }
        double repairCost = InputValidator.readNonNegativeDouble(scanner, "Estimated Repair Cost (RM): ");

        WarrantyClaim claim = warranty.fileClaim(issue, repairCost);
        System.out.println("Claim filed successfully: " + claim);
    }

    /**
     * View all claims filed against an appliance's warranty, and optionally
     * settle one (approve/reject/complete), tracking the resulting repair cost.
     * Every dead end (bad appliance, no claims, bad claim ID, bad status
     * choice) loops back to a retry instead of silently returning to the
     * outer menu, so the person always gets a clear "try again or go back".
     */
    public void manageWarrantyClaims(Scanner scanner, Staff currentStaff) {
        while (true) {
            Appliance appliance = selectApplianceWithWarranty(scanner);
            if (appliance == null) return;

            Warranty warranty = appliance.getWarranty();
            List<WarrantyClaim> claims = warranty.getClaims();
            if (claims.isEmpty()) {
                System.out.println("No claims have been filed for this warranty yet.");
                if (!InputValidator.readYesNo(scanner, "Choose a different appliance? (Y/N): ")) return;
                continue;
            }

            System.out.println("\n=== Claims for Warranty " + warranty.getWarrantyID() + " ===");
            printClaimsTable(claims);
            System.out.printf("Total repair cost (approved/completed): RM%.2f%n", warranty.getTotalRepairCost());

            WarrantyClaim claim = null;
            while (claim == null) {
                System.out.print("\nEnter Claim ID to settle (0 to go back): ");
                String claimID = scanner.nextLine().trim();
                if (claimID.equals("0")) break;
                claim = warranty.findClaimByID(claimID);
                if (claim == null) {
                    System.out.println("No claim found with ID \"" + claimID + "\". Please try again.");
                }
            }
            if (claim == null) {
                if (!InputValidator.readYesNo(scanner, "Choose a different appliance? (Y/N): ")) return;
                continue;
            }

            ClaimStatus newStatus = null;
            while (newStatus == null) {
                System.out.println("\nNew Status:");
                System.out.println("1. APPROVED");
                System.out.println("2. REJECTED");
                System.out.println("3. COMPLETED");
                System.out.println("0. Cancel");
                System.out.print("Select an option: ");
                String choice = scanner.nextLine().trim();
                switch (choice) {
                    case "1": newStatus = ClaimStatus.APPROVED; break;
                    case "2": newStatus = ClaimStatus.REJECTED; break;
                    case "3": newStatus = ClaimStatus.COMPLETED; break;
                    case "0":
                        System.out.println("Cancelled.");
                        return;
                    default:
                        System.out.println("Invalid option — please enter 1, 2, 3, or 0.");
                }
            }
            claim.setStatus(newStatus, currentStaff);
            System.out.println("Claim " + claim.getClaimID() + " updated to " + newStatus + ".");
            return;
        }
    }

    /** Tabular claims listing — same column-based layout as displayInventory(), for a consistent look across every list screen. */
    private void printClaimsTable(List<WarrantyClaim> claims) {
        System.out.printf("%-14s %-32s %-14s %-11s %-15s%n", "Claim ID", "Issue", "Repair Cost", "Status", "Settled By");
        for (WarrantyClaim c : claims) {
            System.out.printf("%-14s %-32s RM%-12.2f %-11s %-15s%n",
                    c.getClaimID(), truncate(c.getIssueDescription(), 32), c.getRepairCost(),
                    c.getStatus(), c.getSettledBy() != null ? c.getSettledBy().getName() : "-");
        }
    }

    /** keeps table columns aligned even if a free-text description runs long. */
    private String truncate(String text, int maxLen) {
        if (text.length() <= maxLen) return text;
        return text.substring(0, maxLen - 3) + "...";
    }

    /** shared prompt used by warranty extension and claim handling to pick an appliance that has an active warranty. */
    private Appliance selectApplianceWithWarranty(Scanner scanner) {
        System.out.println("\n=== Appliances with Active Warranties ===");
        List<Appliance> withWarranty = new ArrayList<>();
        for (Appliance a : applianceManager.getAllAppliances()) {
            if (a.hasWarranty()) withWarranty.add(a);
        }
        if (withWarranty.isEmpty()) {
            System.out.println("No appliances currently have an active warranty.");
            return null;
        }
        printApplianceWarrantyTable(withWarranty);

        Appliance appliance = null;
        while (appliance == null) {
            System.out.print("\nEnter Appliance ID (0 to cancel): ");
            String applianceID = scanner.nextLine().trim();
            if (applianceID.equals("0")) { System.out.println("Cancelled."); return null; }
            appliance = applianceManager.findApplianceByID(applianceID);
            if (appliance == null) {
                System.out.println("No appliance found with ID \"" + applianceID + "\". Please try again.");
                continue;
            }
            if (!appliance.hasWarranty()) {
                System.out.println("This appliance has no active warranty.");
                appliance = null;
            }
        }
        return appliance;
    }

    /** Tabular appliance-with-warranty listing — same layout style as the other list screens. */
    private void printApplianceWarrantyTable(List<Appliance> list) {
        System.out.printf("%-8s %-18s %-12s %-13s %-10s %-8s%n", "ID", "Model", "Brand", "Warranty ID", "Duration", "Extended");
        for (Appliance a : list) {
            Warranty w = a.getWarranty();
            System.out.printf("%-8s %-18s %-12s %-13s %-10s %-8s%n",
                    a.getApplianceID(), a.getModelName(), a.getBrand(), w.getWarrantyID(),
                    w.getDurationMonths() + " mo", w.isExtended() ? "Yes" : "No");
        }
    }
}

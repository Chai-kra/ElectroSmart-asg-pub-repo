import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApplianceManager {
    private List<Appliance> inventory = new ArrayList<>();
    private int nextApplianceNumber = 1;

    /**
     * Auto-generates the next free Appliance ID (A001, A002, ...) instead of
     * asking the admin to type one. Skips over any ID already in use (e.g. the
     * A001-A006 sample data) so it never collides.
     */
    private String generateNextApplianceID() {
        String id;
        do {
            id = String.format("A%03d", nextApplianceNumber++);
        } while (findApplianceByID(id) != null);
        return id;
    }

    public void addAppliance(Scanner scanner) throws DuplicateApplianceException {
        System.out.println("Add Appliance - choose type:");
        System.out.println("1. WhiteGoods");
        System.out.println("2. DigitalGadgets");
        System.out.println("0. Back");
        String type;
        while (true) {
            System.out.print("Select an option: ");
            type = scanner.nextLine().trim();
            if (type.equals("1") || type.equals("2")) break;
            if (type.equals("0")) {
                System.out.println("Cancelled.");
                return;
            }
            System.out.println("Invalid option — please enter 1, 2, or 0.");
        }

        String applianceID = generateNextApplianceID();

        String modelName = readAlphabetOnly(scanner, "Model Name: ");
        String brand = readAlphabetOnly(scanner, "Brand: ");

        double basePrice = readNonNegativeDouble(scanner, "Base Price: ");
        int stockQuantity = readNonNegativeInt(scanner, "Stock Quantity: ");

        Appliance newAppliance;
        if (type.equals("1")) {
            String energyRating = readEnergyRating(scanner);
            String dimension = readDimension(scanner);
            newAppliance = new WhiteGoods(applianceID, modelName, brand, basePrice, stockQuantity, energyRating, dimension);
        } else {
            String operatingSystem = readOperatingSystem(scanner);
            double powerConsumption = readNonNegativeDouble(scanner, "Power Consumption (W): ");
            newAppliance = new DigitalGadgets(applianceID, modelName, brand, basePrice, stockQuantity, operatingSystem, powerConsumption);
        }
        inventory.add(newAppliance);
        System.out.println("Appliance added successfully! Assigned ID: " + applianceID);
        System.out.printf("Base Price RM%.2f + %s RM%.2f = Final Price RM%.2f%n",
                newAppliance.getBasePrice(), newAppliance.getSurchargeLabel(),
                newAppliance.getSurchargeAmount(), newAppliance.calculateFinalPrice());
    }

    /**
     * reads a field that may only contain alphabet letters (and spaces
     * for multi-word values like "Smart TV"). Used for Model Name and Brand.
     */
    private String readAlphabetOnly(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("This field cannot be empty.");
            } else if (!input.matches("[A-Za-z ]+")) {
                System.out.println("Only alphabet letters are allowed (no numbers or symbols).");
            } else {
                return input;
            }
        }
    }

    /** Energy Rating must be a whole number from 1 to 5 (no letters, no negatives). Stored/displayed as "N-star". */
    private String readEnergyRating(Scanner scanner) {
        while (true) {
            System.out.print("Energy Rating (enter a number 1-5): ");
            String input = scanner.nextLine().trim();
            if (!input.matches("\\d+")) {
                System.out.println("Energy rating must be a whole number between 1 and 5 (no letters, no negative numbers).");
                continue;
            }
            int rating = Integer.parseInt(input);
            if (rating < 1 || rating > 5) {
                System.out.println("Energy rating must be between 1 and 5.");
                continue;
            }
            return rating + "-star";
        }
    }

    /** Dimension must follow the LxWxHcm format using positive numbers only, e.g. 180x60x65cm. */
    private String readDimension(Scanner scanner) {
        while (true) {
            System.out.print("Dimension (e.g. 180x60x65cm): ");
            String input = scanner.nextLine().trim();
            if (!input.matches("\\d+(\\.\\d+)?x\\d+(\\.\\d+)?x\\d+(\\.\\d+)?cm")) {
                System.out.println("Invalid format — use positive numbers only, like 180x60x65cm.");
                continue;
            }
            return input;
        }
    }

    /** Operating System must be alphabet letters only, or the literal value "N/A". */
    private String readOperatingSystem(Scanner scanner) {
        while (true) {
            System.out.print("Operating System (or N/A): ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("This field cannot be empty — enter N/A if not applicable.");
            } else if (input.equalsIgnoreCase("N/A")) {
                return "N/A";
            } else if (!input.matches("[A-Za-z ]+")) {
                System.out.println("Only alphabet letters are allowed (no numbers or symbols). Enter N/A if not applicable.");
            } else {
                return input;
            }
        }
    }

    /** strictly accepts only "Y" or "N" (case-insensitive) */
    private boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("Y")) return true;
            if (input.equalsIgnoreCase("N")) return false;
            System.out.println("Please enter Y or N.");
        }
    }

    /**
     * Lets an admin edit an existing appliance's details, including the
     * type-specific fields (energy rating/dimension for WhiteGoods,
     * operating system/power consumption for DigitalGadgets).
     */
    public void editAppliance(Scanner scanner) {
        System.out.print("\nEnter Appliance ID to edit (0 to cancel): ");
        String id = scanner.nextLine().trim();
        if (id.equals("0")) return;
        Appliance a = findApplianceByID(id);
        if (a == null) {
            System.out.println("No appliance found with ID \"" + id + "\".");
            return;
        }

        System.out.println("Editing: " + a);
        System.out.println("1. Model Name");
        System.out.println("2. Brand");
        System.out.println("3. Base Price");
        System.out.println("4. Stock Quantity");
        if (a instanceof WhiteGoods) {
            System.out.println("5. Energy Rating");
            System.out.println("6. Dimension");
        } else if (a instanceof DigitalGadgets) {
            System.out.println("5. Operating System");
            System.out.println("6. Power Consumption");
        }
        System.out.println("0. Cancel");
        System.out.print("Field to edit: ");
        String field = scanner.nextLine().trim();

        try {
            switch (field) {
                case "1":
                    a.setModelName(readAlphabetOnly(scanner, "New Model Name: "));
                    break;
                case "2":
                    a.setBrand(readAlphabetOnly(scanner, "New Brand: "));
                    break;
                case "3":
                    a.setBasePrice(readNonNegativeDouble(scanner, "New Base Price: "));
                    break;
                case "4":
                    a.setStockQuantity(readNonNegativeInt(scanner, "New Stock Quantity: "));
                    break;
                case "5":
                    if (a instanceof WhiteGoods wg) {
                        wg.setEnergyRating(readEnergyRating(scanner));
                    } else if (a instanceof DigitalGadgets dg) {
                        dg.setOperatingSystem(readOperatingSystem(scanner));
                    } else {
                        System.out.println("Invalid option.");
                        return;
                    }
                    break;
                case "6":
                    if (a instanceof WhiteGoods wg) {
                        wg.setDimension(readDimension(scanner));
                    } else if (a instanceof DigitalGadgets dg) {
                        dg.setPowerConsumption(readNonNegativeDouble(scanner, "New Power Consumption: "));
                    } else {
                        System.out.println("Invalid option.");
                        return;
                    }
                    break;
                case "0":
                    System.out.println("Cancelled.");
                    return;
                default:
                    System.out.println("Invalid option.");
                    return;
            }
            System.out.println("Appliance updated: " + a);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public Appliance findApplianceByID(String applianceID) {
        for (Appliance a : inventory) {
            if (a.getApplianceID().equalsIgnoreCase(applianceID)) {
                return a;
            }
        }
        return null;
    }

    public List<Appliance> getAllAppliances() {
        return inventory;
    }

    public void clearInventory() {
        inventory.clear();
    }

    public void viewLowStock(Scanner scanner) {
        System.out.println("\nLow stock appliances (below 3 units):");
        boolean found = false;
        for (Appliance a : inventory) {
            if (a.isLowStock()) {
                System.out.println(a.getApplianceID() + " | " + a.getModelName() + " - Stock: " + a.getStockQuantity());
                found = true;
            }
        }
        if (!found) {
            System.out.println("No appliances are currently low on stock.");
        }
        pause(scanner);
    }

    public void displayInventory() {
        System.out.println("\n=== White Goods ===");
        System.out.printf("%-8s %-18s %-12s %-10s %-14s %-15s %-6s%n", "ID", "Model", "Brand", "Energy", "Dimension", "Price", "Stock");
        for (Appliance a : inventory) {
            if (a instanceof WhiteGoods wg) {
                System.out.printf("%-8s %-18s %-12s %-10s %-14s RM%-13.2f %-6d%n",
                    wg.getApplianceID(), wg.getModelName(), wg.getBrand(), wg.getEnergyRating(), wg.getDimension(),
                    wg.calculateFinalPrice(), wg.getStockQuantity());
            }
        }
        System.out.println("\n=== Digital Gadgets ===");
        System.out.printf("%-8s %-18s %-12s %-15s %-14s %-15s %-6s%n", "ID", "Model", "Brand", "OS", "Power(W)", "Price", "Stock");
        for (Appliance a : inventory) {
            if (a instanceof DigitalGadgets dg) {
                System.out.printf("%-8s %-18s %-12s %-15s %-14.1f RM%-13.2f %-6d%n",
                    dg.getApplianceID(), dg.getModelName(), dg.getBrand(), dg.getOperatingSystem(), dg.getPowerConsumption(),
                    dg.calculateFinalPrice(), dg.getStockQuantity());
            }
        }
    }

    public void processSale(Scanner scanner, CustomerManager customerManager, StaffManager staffManager, Staff currentStaff)
            throws InvalidWarrantyExtensionException {
        System.out.println("\n=== Available Appliances ===");
        if (inventory.isEmpty()) {
            System.out.println("No appliances in inventory yet.");
            return;
        }
        displayInventory();

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
                appliance = findApplianceByID(applianceID);
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
            if (!readYesNo(scanner, "Add another item? (Y/N): ")) addingItems = false;
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
            // now shows the base price + surcharge/levy breakdown per unit,
            // instead of just the already-marked-up line total, so it's clear the
            // WhiteGoods delivery surcharge / DigitalGadgets recycling levy is applied.
            System.out.printf("%d x %-20s Base RM%.2f + %s RM%.2f = RM%.2f each -> RM%.2f%n",
                    qty, a.getModelName(), a.getBasePrice(), a.getSurchargeLabel(),
                    a.getSurchargeAmount(), a.calculateFinalPrice(), a.calculateFinalPrice() * qty);
        }
        System.out.printf("Subtotal: RM%.2f%n", runningTotal);
        System.out.printf("Membership Discount (%s): -%.0f%%%n", customer.getMemberShipStatus(), discount * 100);
        System.out.printf("Total: RM%.2f%n", finalTotal);
        if (!readYesNo(scanner, "Confirm sale? (Y/N): ")) {
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

        // Uses the logged-in staff passed from Driver.java
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

    /**
     * NEW: File a repair claim against an appliance's active warranty.
     */
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
        double repairCost = readNonNegativeDouble(scanner, "Estimated Repair Cost (RM): ");

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
                if (!readYesNo(scanner, "Choose a different appliance? (Y/N): ")) return;
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
                if (!readYesNo(scanner, "Choose a different appliance? (Y/N): ")) return;
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

    /** NEW: shared prompt used by warranty extension and claim handling to pick an appliance that has an active warranty. */
    private Appliance selectApplianceWithWarranty(Scanner scanner) {
        System.out.println("\n=== Appliances with Active Warranties ===");
        List<Appliance> withWarranty = new ArrayList<>();
        for (Appliance a : inventory) {
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
            appliance = findApplianceByID(applianceID);
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

    public void loadSampleData() {
        inventory.add(new WhiteGoods("A001", "Fridge X1", "Samsung", 1500.00, 10, "5-star", "180x60x65cm"));
        inventory.add(new WhiteGoods("A002", "Washer Z3", "LG", 1200.00, 1, "4-star", "60x60x85cm"));
        inventory.add(new WhiteGoods("A003", "Freezer F7", "Panasonic", 999.00, 5, "5-star", "150x55x60cm"));
        inventory.add(new DigitalGadgets("A004", "Smart TV Y2", "Sony", 2000.00, 2, "Android TV", 150.0));
        inventory.add(new DigitalGadgets("A005", "Soundbar S1", "JBL", 450.00, 8, "N/A", 60.0));
        inventory.add(new DigitalGadgets("A006", "Smart TV Q9", "Samsung", 3200.00, 1, "Tizen OS", 180.0));
        nextApplianceNumber = 7;
    }

    private double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number — please enter digits only (e.g. 100 or 100.5).");
            }
        }
    }

    private int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number — please enter a whole number only.");
            }
        }
    }

    private double readNonNegativeDouble(Scanner scanner, String prompt) {
        while (true) {
            double value = readDouble(scanner, prompt);
            if (value < 0) {
                System.out.println("Value cannot be negative.");
                continue;
            }
            return value;
        }
    }

    private int readNonNegativeInt(Scanner scanner, String prompt) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value < 0) {
                System.out.println("Value cannot be negative.");
                continue;
            }
            return value;
        }
    }

    private void pause(Scanner scanner) {
        System.out.println("\nPress Enter to return to the menu...");
        scanner.nextLine();
    }
}
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApplianceManager {
    private List<Appliance> inventory = new ArrayList<>();

    public void addAppliance(Scanner scanner) throws DuplicateApplianceException {
        System.out.println("Add Appliance - choose type:");
        System.out.println("1. WhiteGoods");
        System.out.println("2. DigitalGadgets");
        String type;
        while (true) {
            System.out.print("Select an option: ");
            type = scanner.nextLine().trim();
            if (type.equals("1") || type.equals("2")) break;
            System.out.println("Invalid option — please enter 1 or 2.");
        }

        String applianceID;
        while (true) {
            System.out.print("Appliance ID: ");
            applianceID = scanner.nextLine().trim();
            if (applianceID.isEmpty()) {
                System.out.println("Appliance ID cannot be empty.");
            } else if (findApplianceByID(applianceID) != null) {
                System.out.println("An appliance with ID " + applianceID + " already exists. Try a different ID.");
            } else {
                break;
            }
        }

        String modelName;
        while (true) {
            System.out.print("Model Name: ");
            modelName = scanner.nextLine().trim();
            if (modelName.isEmpty()) System.out.println("Model name cannot be empty.");
            else break;
        }

        String brand;
        while (true) {
            System.out.print("Brand: ");
            brand = scanner.nextLine().trim();
            if (brand.isEmpty()) System.out.println("Brand cannot be empty.");
            else break;
        }

        double basePrice = readNonNegativeDouble(scanner, "Base Price: ");
        int stockQuantity = readNonNegativeInt(scanner, "Stock Quantity: ");

        Appliance newAppliance;
        if (type.equals("1")) {
            String energyRating;
            while (true) {
                System.out.print("Energy Rating (e.g. 5-star): ");
                energyRating = scanner.nextLine().trim();
                if (energyRating.isEmpty()) System.out.println("Energy rating cannot be empty.");
                else break;
            }
            String dimension;
            while (true) {
                System.out.print("Dimension (e.g. 180x60x65cm): ");
                dimension = scanner.nextLine().trim();
                if (dimension.isEmpty()) System.out.println("Dimension cannot be empty.");
                else break;
            }
            newAppliance = new WhiteGoods(applianceID, modelName, brand, basePrice, stockQuantity, energyRating, dimension);
        } else {
            String operatingSystem;
            while (true) {
                System.out.print("Operating System (or N/A): ");
                operatingSystem = scanner.nextLine().trim();
                if (operatingSystem.isEmpty()) System.out.println("Operating system cannot be empty — enter N/A if not applicable.");
                else break;
            }
            double powerConsumption = readNonNegativeDouble(scanner, "Power Consumption (W): ");
            newAppliance = new DigitalGadgets(applianceID, modelName, brand, basePrice, stockQuantity, operatingSystem, powerConsumption);
        }
        inventory.add(newAppliance);
        System.out.println("Appliance added successfully!");
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
                    System.out.print("New Model Name: ");
                    a.setModelName(scanner.nextLine().trim());
                    break;
                case "2":
                    System.out.print("New Brand: ");
                    a.setBrand(scanner.nextLine().trim());
                    break;
                case "3":
                    a.setBasePrice(readNonNegativeDouble(scanner, "New Base Price: "));
                    break;
                case "4":
                    a.setStockQuantity(readNonNegativeInt(scanner, "New Stock Quantity: "));
                    break;
                case "5":
                    if (a instanceof WhiteGoods wg) {
                        System.out.print("New Energy Rating: ");
                        wg.setEnergyRating(scanner.nextLine().trim());
                    } else if (a instanceof DigitalGadgets dg) {
                        System.out.print("New Operating System: ");
                        dg.setOperatingSystem(scanner.nextLine().trim());
                    } else {
                        System.out.println("Invalid option.");
                        return;
                    }
                    break;
                case "6":
                    if (a instanceof WhiteGoods wg) {
                        System.out.print("New Dimension: ");
                        wg.setDimension(scanner.nextLine().trim());
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
        System.out.printf("%-8s %-18s %-10s %-14s %-15s %-6s%n", "ID", "Model", "Energy", "Dimension", "Price", "Stock");
        for (Appliance a : inventory) {
            if (a instanceof WhiteGoods wg) {
                System.out.printf("%-8s %-18s %-10s %-14s RM%-13.2f %-6d%n",
                    wg.getApplianceID(), wg.getModelName(), wg.getEnergyRating(), wg.getDimension(),
                    wg.calculateFinalPrice(), wg.getStockQuantity());
            }
        }
        System.out.println("\n=== Digital Gadgets ===");
        System.out.printf("%-8s %-18s %-15s %-14s %-15s %-6s%n", "ID", "Model", "OS", "Power(W)", "Price", "Stock");
        for (Appliance a : inventory) {
            if (a instanceof DigitalGadgets dg) {
                System.out.printf("%-8s %-18s %-15s %-14.1f RM%-13.2f %-6d%n",
                    dg.getApplianceID(), dg.getModelName(), dg.getOperatingSystem(), dg.getPowerConsumption(),
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

            int quantity = 0;
            while (true) {
                System.out.print("Quantity for " + appliance.getModelName() + " (Available: " + appliance.getStockQuantity() + "): ");
                String input = scanner.nextLine().trim();
                try {
                    quantity = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number — please enter a whole number.");
                    continue;
                }
                if (quantity <= 0) { System.out.println("Quantity must be at least 1."); continue; }
                if (quantity > appliance.getStockQuantity()) {
                    System.out.println("Not enough stock — only " + appliance.getStockQuantity() + " available.");
                    continue;
                }
                break;
            }

            cartAppliances.add(appliance);
            cartQuantities.add(quantity);
            double lineTotal = appliance.calculateFinalPrice() * quantity;
            runningTotal += lineTotal;
            System.out.printf("Added: %d x %s (RM%.2f)%n", quantity, appliance.getModelName(), lineTotal);
            System.out.print("Add another item? (Y/N): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) addingItems = false;
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
            System.out.printf("%d x %-20s RM%.2f%n", qty, a.getModelName(), a.calculateFinalPrice() * qty);
        }
        System.out.printf("Subtotal: RM%.2f%n", runningTotal);
        System.out.printf("Membership Discount (%s): -%.0f%%%n", customer.getMemberShipStatus(), discount * 100);
        System.out.printf("Total: RM%.2f%n", finalTotal);
        System.out.print("Confirm sale? (Y/N): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("Y")) {
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
        System.out.println("\n=== Appliances with Active Warranties ===");
        boolean anyActive = false;
        for (Appliance a : inventory) {
            if (a.hasWarranty()) {
                System.out.println(a.getApplianceID() + " | " + a + " | Warranty: " + a.getWarranty().getDurationMonths() + " months");
                anyActive = true;
            }
        }
        if (!anyActive) {
            System.out.println("No appliances currently have an active warranty.");
            return;
        }

        Appliance appliance = null;
        while (appliance == null) {
            System.out.print("\nEnter Appliance ID (0 to cancel): ");
            String applianceID = scanner.nextLine().trim();
            if (applianceID.equals("0")) { System.out.println("Cancelled."); return; }
            appliance = findApplianceByID(applianceID);
            if (appliance == null) {
                System.out.println("No appliance found with ID \"" + applianceID + "\". Please try again.");
                continue;
            }
            if (!appliance.hasWarranty()) {
                System.out.println("This appliance has no active warranty to extend.");
                appliance = null;
            }
        }

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

    public void loadSampleData() {
        inventory.add(new WhiteGoods("A001", "Fridge X1", "Samsung", 1500.00, 10, "5-star", "180x60x65cm"));
        inventory.add(new WhiteGoods("A002", "Washer Z3", "LG", 1200.00, 1, "4-star", "60x60x85cm"));
        inventory.add(new WhiteGoods("A003", "Freezer F7", "Panasonic", 999.00, 5, "5-star", "150x55x60cm"));
        inventory.add(new DigitalGadgets("A004", "Smart TV Y2", "Sony", 2000.00, 2, "Android TV", 150.0));
        inventory.add(new DigitalGadgets("A005", "Soundbar S1", "JBL", 450.00, 8, "N/A", 60.0));
        inventory.add(new DigitalGadgets("A006", "Smart TV Q9", "Samsung", 3200.00, 1, "Tizen OS", 180.0));
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
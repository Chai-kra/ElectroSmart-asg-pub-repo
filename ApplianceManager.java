import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Owns the appliance inventory: adding, editing, listing, and low-stock
 * checks. Sale processing and warranty/claims handling live in
 * WarrantyManager, which reads from this class instead of duplicating the
 * inventory list — keeps this class to a single responsibility (inventory
 * management) instead of also owning sales and warranty logic.
 */
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

        String modelName = InputValidator.readAlphabetOnly(scanner, "Model Name: ");
        String brand = InputValidator.readAlphabetOnly(scanner, "Brand: ");

        double basePrice = InputValidator.readNonNegativeDouble(scanner, "Base Price: ");
        int stockQuantity = InputValidator.readNonNegativeInt(scanner, "Stock Quantity: ");

        Appliance newAppliance;
        if (type.equals("1")) {
            String energyRating = InputValidator.readEnergyRating(scanner);
            String dimension = InputValidator.readDimension(scanner);
            newAppliance = new WhiteGoods(applianceID, modelName, brand, basePrice, stockQuantity, energyRating, dimension);
        } else {
            String operatingSystem = InputValidator.readOperatingSystem(scanner);
            double powerConsumption = InputValidator.readNonNegativeDouble(scanner, "Power Consumption (W): ");
            newAppliance = new DigitalGadgets(applianceID, modelName, brand, basePrice, stockQuantity, operatingSystem, powerConsumption);
        }
        inventory.add(newAppliance);
        System.out.println("Appliance added successfully! Assigned ID: " + applianceID);
        System.out.printf("Base Price RM%.2f + %s RM%.2f = Final Price RM%.2f%n",
                newAppliance.getBasePrice(), newAppliance.getSurchargeLabel(),
                newAppliance.getSurchargeAmount(), newAppliance.calculateFinalPrice());
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
                    a.setModelName(InputValidator.readAlphabetOnly(scanner, "New Model Name: "));
                    break;
                case "2":
                    a.setBrand(InputValidator.readAlphabetOnly(scanner, "New Brand: "));
                    break;
                case "3":
                    a.setBasePrice(InputValidator.readNonNegativeDouble(scanner, "New Base Price: "));
                    break;
                case "4":
                    a.setStockQuantity(InputValidator.readNonNegativeInt(scanner, "New Stock Quantity: "));
                    break;
                case "5":
                    if (a instanceof WhiteGoods wg) {
                        wg.setEnergyRating(InputValidator.readEnergyRating(scanner));
                    } else if (a instanceof DigitalGadgets dg) {
                        dg.setOperatingSystem(InputValidator.readOperatingSystem(scanner));
                    } else {
                        System.out.println("Invalid option.");
                        return;
                    }
                    break;
                case "6":
                    if (a instanceof WhiteGoods wg) {
                        wg.setDimension(InputValidator.readDimension(scanner));
                    } else if (a instanceof DigitalGadgets dg) {
                        dg.setPowerConsumption(InputValidator.readNonNegativeDouble(scanner, "New Power Consumption: "));
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
        InputValidator.pause(scanner);
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

    public void loadSampleData() {
        inventory.add(new WhiteGoods("A001", "Fridge X1", "Samsung", 1500.00, 10, "5-star", "180x60x65cm"));
        inventory.add(new WhiteGoods("A002", "Washer Z3", "LG", 1200.00, 1, "4-star", "60x60x85cm"));
        inventory.add(new WhiteGoods("A003", "Freezer F7", "Panasonic", 999.00, 5, "5-star", "150x55x60cm"));
        inventory.add(new DigitalGadgets("A004", "Smart TV Y2", "Sony", 2000.00, 2, "Android TV", 150.0));
        inventory.add(new DigitalGadgets("A005", "Soundbar S1", "JBL", 450.00, 8, "N/A", 60.0));
        inventory.add(new DigitalGadgets("A006", "Smart TV Q9", "Samsung", 3200.00, 1, "Tizen OS", 180.0));
        nextApplianceNumber = 7;
    }
}

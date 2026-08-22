import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApplianceManager {
    private List<Appliance> inventory = new ArrayList<>();

public void addAppliance(Scanner scanner) throws DuplicateApplianceException {
        System.out.println("Add Appliance - choose type:");
        System.out.println("1. WhiteGoods");
        System.out.println("2. DigitalGadgets");
        System.out.print("Select an option: ");
        String type = scanner.nextLine();

        System.out.print("Appliance ID: ");
        String applianceID = scanner.nextLine();
        // duplicate check
        if (findApplianceByID(applianceID) != null) {
            throw new DuplicateApplianceException("An appliance with ID " + applianceID + " already exists.");
        }

        System.out.print("Model Name: ");
        String modelName = scanner.nextLine();
        System.out.print("Brand: ");
        String brand = scanner.nextLine();
        double basePrice = readDouble(scanner, "Base Price: ");
        int stockQuantity = readInt(scanner, "Stock Qunatity: ");

        Appliance newAppliance;

        if (type.equals("1")) {
            System.out.print("Energy Rating (1-5): ");
            String energyRating = scanner.nextLine();
            System.out.print("Dimension: ");
            String dimension = scanner.nextLine();
            newAppliance = new WhiteGoods(applianceID, modelName, brand, basePrice, stockQuantity, energyRating, dimension);
        } else {
            System.out.print("Operating System: ");
            String operatingSystem = scanner.nextLine();
            double powerConsumption = readDouble(scanner, "Power Consumption: ");
            newAppliance = new DigitalGadgets(applianceID, modelName, brand, basePrice, stockQuantity, operatingSystem, powerConsumption);
        }

        inventory.add(newAppliance);
        System.out.println("Appliance added successfully!");
    }

    public void viewLowStock(Scanner scanner) {
        System.out.println("Low stock appliances (below 3 units):");
        for (Appliance a : inventory) {
            if (a.isLowStock()) {
                System.out.println(a.getModelName() + " - Stock: " + a.getStockQuantity());
            }
        }
        pause(scanner);
    }

    // SALES
    public void processSale(Scanner scanner, CustomerManager customerManager, StaffManager staffManager)
            throws InvalidWarrantyExtensionException {

        System.out.println("\n=== Available Appliances ===");
        if (inventory.isEmpty()) {
            System.out.println("No appliances in inventory yet.");
            return;
        }
        for (Appliance a : inventory) {
            System.out.println(a.getApplianceID() + " | " + a);
        }

        Appliance appliance = null;
        while (appliance == null) {
            System.out.print("\nEnter Appliance ID (0 to cancel): ");
            String applianceID = scanner.nextLine();
            if (applianceID.equals("0")) { System.out.println("Sale cancelled."); return; }
            appliance = findApplianceByID(applianceID);
            if (appliance == null) {
                System.out.println("No appliance found with ID \"" + applianceID + "\". Please try again.");
            }
        }

        Customer customer = null;
        while (customer == null) {
            System.out.print("Enter Customer ID (0 to cancel): ");
            String customerID = scanner.nextLine();
            if (customerID.equals("0")) { System.out.println("Sale cancelled."); return; }
            customer = customerManager.findByID(customerID);
            if (customer == null) {
                System.out.println("No customer found with ID \"" + customerID + "\". Register them first, or try again.");
            }
        }

        Staff staff = null;
        while (staff == null) {
            System.out.print("Enter Staff ID (0 to cancel): ");
            String staffID = scanner.nextLine();
            if (staffID.equals("0")) { System.out.println("Sale cancelled."); return; }
            staff = staffManager.findByID(staffID);
            if (staff == null) {
                System.out.println("No staff found with ID \"" + staffID + "\". Please try again.");
            }
        }

        int quantity = 0;
        while (true) {
            System.out.print("Enter Quantity (Available: " + appliance.getStockQuantity() + "): ");
            String input = scanner.nextLine();
            try {
                quantity = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number — please enter a whole number.");
                continue;
            }
            if (quantity <= 0) {
                System.out.println("Quantity must be at least 1.");
                continue;
            }
            if (quantity > appliance.getStockQuantity()) {
                System.out.println("Not enough stock — only " + appliance.getStockQuantity() + " available. Enter a smaller amount.");
                continue;
            }
            break;
        }

        double unitPrice = appliance.calculateFinalPrice();
        double discount = customer.getDiscountRate();
        double totalPrice = unitPrice * quantity * (1 - discount);

        appliance.reduceStock(quantity);
        appliance.activateWarranty(staff);

        Transaction transaction = new Transaction(appliance.getApplianceID(), customer.getCustomerID(), quantity, totalPrice, staff);
        staffManager.recordSale(transaction);

        System.out.println("\n=== Sale Complete ===");
        System.out.printf("%d x %s%n", quantity, appliance.getModelName());
        System.out.println("Customer: " + customer.getName() + " (" + customer.getMemberShipStatus() + ")");
        System.out.println("Staff: " + staff.getName());
        System.out.printf("Total charged: RM%.2f%n", totalPrice);
    }


    public void loadSampleData() {
        inventory.add(new WhiteGoods("A001", "Fridge X1", "Samsung", 1500.00, 10, "5-star", "180x60x65cm"));
        inventory.add(new DigitalGadgets("A002", "Smart TV Y2", "Sony", 2000.00, 2, "Android TV", 150.0));
        inventory.add(new WhiteGoods("A003", "Washer Z3", "LG", 1200.00, 1, "4-star", "60x60x85cm"));
    }

    public Appliance findApplianceByID(String applianceID) {
        for (Appliance a : inventory) {
            if (a.getApplianceID().equals(applianceID)) {
                return a;
            }
        }
        return null; // not found
    }

    // =========================================================
    // HELPER
    // =========================================================
    // Helper: Validation - Invalid number(double) 
    private double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number — please enter digits only (e.g. 100 or 100.5).");
            }
        }
    }

    // Helper: Validation - Invalid number(int) 
    private int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number — please enter a whole number only.");
            }
        }
    }

    // Helper: press enter to return
    private void pause(Scanner scanner) {
        System.out.println("\nPress Enter to return to the menu...");
        scanner.nextLine();
    }
}
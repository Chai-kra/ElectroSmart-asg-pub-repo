import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApplianceManager {
    private List<Appliance> inventory = new ArrayList<>();

    public void addAppliance(Scanner scanner) {
        System.out.println("Add Appliance - choose type:");
        System.out.println("1. WhiteGoods");
        System.out.println("2. DigitalGadgets");
        System.out.print("Select an option: ");
        String type = scanner.nextLine();

        System.out.print("Appliance ID: ");
        String applianceID = scanner.nextLine();
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
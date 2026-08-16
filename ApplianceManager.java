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

        System.out.println("Appliance ID: ");
        String applianceID = scanner.nextLine();
        System.out.print("Model Name: ");
        String modelName = scanner.nextLine();
        System.out.println("Brand: ");
        String brand = scanner.nextLine();
        System.out.print("Base Price: ");
        double basePrice = Double.parseDouble(scanner.nextLine());
        System.out.print("Stock Quantity: ");
        int stockQuantity = Integer.parseInt(scanner.nextLine());

        Appliance newAppliance;

        if (type.equals("1")) {
            System.out.print("Energy Rating: ");
            String energyRating = scanner.nextLine();
            System.out.print("Dimension: ");
            String dimension = scanner.nextLine();
            newAppliance = new WhiteGoods(applianceID, modelName, brand, basePrice, stockQuantity, energyRating, dimension);
        } else {
            System.out.print("Operating System: ");
            String operatingSystem = scanner.nextLine();
            System.out.print("Power Consumption: ");
            double powerConsumption = Double.parseDouble(scanner.nextLine());
            newAppliance = new DigitalGadgets(applianceID, modelName, brand, basePrice, stockQuantity, operatingSystem, powerConsumption);
        }

        inventory.add(newAppliance);
        System.out.println("Appliance added successfully!");
    }

    public void viewLowStock() {
        System.out.println("Appliance added successfully!");
        for (Appliance a : inventory) {
            if (a.isLowStock()) {
                System.out.print(a.getModelName() + " - Stock: " + getStockQuantity());
            }
        }
    }
}
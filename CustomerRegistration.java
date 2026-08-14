import java.util.Scanner;

public class CustomerRegistration {

    public static void registerCustomer(Scanner sc, CustomerManager manager) {

        System.out.println("=== Register New Customer ===");

        // Customer ID verification
        String customerID;

        while (true) {

            System.out.print("Enter Customer ID (e.g. CUS001): ");
            customerID = sc.nextLine().trim();

            if (customerID.isEmpty()) {

                System.out.println(
                    "Customer ID cannot be empty. Try again."
                );

            } else if (manager.findByID(customerID) != null) {

                System.out.println(
                    "This Customer ID already exists. Try again."
                );

            } else {
                break;
            }
        }


        // Name verification
        String name;

        while (true) {

            System.out.print("Enter Name: ");
            name = sc.nextLine().trim();

            if (name.isEmpty()) {

                System.out.println(
                    "Name cannot be empty. Try again."
                );

            } else {
                break;
            }
        }


        // Email verification
        String email;

        while (true) {

            System.out.print("Enter Email: ");
            email = sc.nextLine().trim();

            if (!email.matches(
                    "^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {

                System.out.println(
                    "Invalid email format. Try again."
                );

            } else {
                break;
            }
        }


        // Membership status verification
        MembershipStatus status = null;

        while (status == null) {

            System.out.print(
                "Enter Membership Status (Regular/Silver/Gold): "
            );

            String input = sc.nextLine().trim().toUpperCase();

            try {

                status = MembershipStatus.valueOf(input);

            } catch (IllegalArgumentException e) {

                System.out.println(
                    "Invalid status. Please type Regular, Silver, or Gold."
                );
            }
        }


        // Create and add customer
        try {

            Customer newCustomer = new Customer(
                customerID,
                name,
                email,
                status
            );

            manager.addCustomer(newCustomer);

            System.out.println(
                "Customer registered successfully!"
            );

            System.out.println(newCustomer);

        } catch (DuplicateCustomerException e) {

            System.out.println(
                "Error: " + e.getMessage()
            );

        } catch (IllegalArgumentException e) {

            System.out.println(
                "Error: " + e.getMessage()
            );
        }
    }
}
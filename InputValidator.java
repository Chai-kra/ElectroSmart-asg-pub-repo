import java.util.Scanner;

/**
 * Shared console-input validation helpers. Pulled out of ApplianceManager so
 * that both ApplianceManager (inventory CRUD) and WarrantyManager (sales +
 * warranty + claims) can reuse the same validated prompts instead of each
 * keeping its own copy — one source of truth for "how do we validate a
 * price / a Y-N answer / etc.", and each manager stays focused on its own
 * responsibility instead of also owning input-parsing logic.
 */
public final class InputValidator {

    /**
     * Single source of truth for the "name@domain.tld" email format —
     * referenced here (for the menu-level prompt) and from Customer.java /
     * Staff.java (for the object-level setter check), instead of the same
     * literal regex being copy-pasted in three separate files.
     */
    public static final String EMAIL_REGEX = "^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$";

    private InputValidator() {
        // static utility class — never instantiated
    }

    /** reads a field that may only contain alphabet letters (and spaces for multi-word values like "Smart TV"). */
    public static String readAlphabetOnly(Scanner scanner, String prompt) {
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
    public static String readEnergyRating(Scanner scanner) {
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
    public static String readDimension(Scanner scanner) {
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
    public static String readOperatingSystem(Scanner scanner) {
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
    public static boolean readYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("Y")) return true;
            if (input.equalsIgnoreCase("N")) return false;
            System.out.println("Please enter Y or N.");
        }
    }

    public static double readDouble(Scanner scanner, String prompt) {
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

    public static int readInt(Scanner scanner, String prompt) {
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

    public static double readNonNegativeDouble(Scanner scanner, String prompt) {
        while (true) {
            double value = readDouble(scanner, prompt);
            if (value < 0) {
                System.out.println("Value cannot be negative.");
                continue;
            }
            return value;
        }
    }

    public static int readNonNegativeInt(Scanner scanner, String prompt) {
        while (true) {
            int value = readInt(scanner, prompt);
            if (value < 0) {
                System.out.println("Value cannot be negative.");
                continue;
            }
            return value;
        }
    }

    public static void pause(Scanner scanner) {
        System.out.println("\nPress Enter to return to the menu...");
        scanner.nextLine();
    }
}

package main.java.com.barakaguard.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;

public class InputUtil {

    private static final Scanner scanner = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    // Lire un texte
    public static String readString(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    // Lire un entier
    public static int readInt(String message) {
        while (true) {
            System.out.print(message + " : ");
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrez un entier valide !");
            }
        }
    }

        // Lire un entier
    public static long readLong(String message) {
        while (true) {
            System.out.print(message + " : ");
            String input = scanner.nextLine().trim();
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrez un entier valide !");
            }
        }
    }

    public static int readInt(String message, int min, int max) {
        while (true) {
            System.out.print(message + " [" + min + " - " + max + "] : ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value < min || value > max) {
                    System.out.println("La valeur doit être entre " + min + " et " + max + ".");
                } else {
                    return value;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrez un entier valide !");
            }
        }
    }

    // Lire un double
    public static double readDouble(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrez un nombre valide !");
            }
        }
    }

    public static int readInt(String message, char sign) {
        while (true) {
            System.out.print(message + " : ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);

                switch (sign) {
                    case '+': // positif uniquement
                        if (value <= 0) {
                            System.out.println("Entrez un nombre positif !");
                            continue;
                        }
                        break;
                    case '-': // négatif uniquement
                        if (value >= 0) {
                            System.out.println("Entrez un nombre négatif !");
                            continue;
                        }
                        break;
                    case '*': // aucun signe imposé
                        break;
                    default:
                        System.out.println("Signe invalide !");
                        continue;
                }

                return value;

            } catch (NumberFormatException e) {
                System.out.println("Entrez un entier valide !");
            }
        }
    }

    // Lire une date (format yyyy-MM-dd)
    public static LocalDate readDate(String message) {
        while (true) {
            System.out.print(message + " (format yyyy-MM-dd) : ");
            String input = scanner.nextLine().trim();
            try {
                return LocalDate.parse(input, DATE_FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Format de date invalide !");
            }
        }
    }

    // Lire un UUID
    public static UUID readUUID(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim();
            try {
                return UUID.fromString(input);
            } catch (IllegalArgumentException e) {
                System.out.println("UUID invalide !");
            }
        }
    }

    // Lire un choix (menu)
    public static String readChoice(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    public static Double readDoubleOptional(String message) {
        System.out.print(message + " (laisser vide pour ne pas changer) : ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty())
            return null;
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Entrez un nombre valide !");
            return readDoubleOptional(message);
        }
    }

    public static LocalDate readDateOptional(String message) {
        System.out.print(message + " (laisser vide pour ne pas changer) : ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return null; // pas de modification
        }
        try {
            return LocalDate.parse(input); // yyyy-MM-dd
        } catch (Exception e) {
            System.out.println("Date invalide, réessayez !");
            return readDateOptional(message);
        }
    }

    public static boolean readYesNo(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim().toLowerCase();

            if (input.equals("o") || input.equals("oui") || input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("non") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Réponse invalide. Tapez 'o' pour oui ou 'n' pour non.");
            }
        }
    }

    public static String readEmail(String message) {
        String email;
        while (true) {
            System.out.print(message);
            email = scanner.nextLine().trim();

            if (EMAIL_PATTERN.matcher(email).matches()) {
                return email;
            } else {
                System.err.println("❌ Email invalide, veuillez réessayer (ex: user@example.com).");
            }
        }
    }

    public static String readString(String prompt, String... options) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print(prompt + " ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                return null;
            }

            if (options != null && options.length > 0) {
                for (String option : options) {
                    if (option.equalsIgnoreCase(input)) {
                        return option.toUpperCase();
                    }
                }
                System.out.println("❌ Valeur invalide. Choix possibles : " + String.join(", ", options));
            } else {
                return input;
            }
        }
    }

    public static <E extends Enum<E>> E readEnum(String prompt, Class<E> enumClass) {
        E[] values = enumClass.getEnumConstants();
        String[] options = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            options[i] = values[i].name();
        }

        String input = readString(prompt + " (laisser vide si aucun)", options);

        if (input == null) {
            return null;
        } else {
            return Enum.valueOf(enumClass, input);
        }
    }

}

package main.java.com.barakaguard.util;

import java.util.Set;

public class CountryExtractor {

    private static final Set<String> COUNTRIES = Set.of(
            "MAROC", "ESPAGNE", "ITALIE", "ALLEMAGNE", "FRANCE", "USA",
            "CANADA", "BELGIQUE", "TUNISIE", "ALGÉRIE"
    );

    public static String extractCountry(String lieu) {
        if (lieu == null || lieu.isBlank())
            return "UNKNOWN";

        String[] parts = lieu.split(",");
        for (String part : parts) {
            String candidate = part.trim().toUpperCase();
            if (COUNTRIES.contains(candidate)) {
                return candidate;
            }
        }
        return parts[parts.length - 1].trim().toUpperCase();
    }
}

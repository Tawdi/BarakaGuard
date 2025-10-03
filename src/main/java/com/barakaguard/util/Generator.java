package main.java.com.barakaguard.util;

import java.util.Random;

public class Generator {

    private static final Random random = new Random();

    public static String generateCodeNumber(String prefix, int size) {
        StringBuilder sb = new StringBuilder(prefix+"-");
        for (int i = 0; i < size; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}

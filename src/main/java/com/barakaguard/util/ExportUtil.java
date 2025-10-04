package main.java.com.barakaguard.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class ExportUtil {

public static void exportJSON(String[] headers, Iterable<String[]> rows, String fileName) {
    ensureExportFolder();
    Path path = Path.of("exports", fileName);
    try (FileWriter writer = new FileWriter(path.toFile())) {
        writer.write("[\n");
        int i = 0;
        for (String[] row : rows) {
            writer.write("  {\n");
            for (int j = 0; j < headers.length; j++) {
                writer.write("    \"" + headers[j] + "\": \"" + row[j] + "\"");
                if (j < headers.length - 1) writer.write(",");
                writer.write("\n");
            }
            writer.write("  }");
            i++;
            if (i < ((List<String[]>) rows).size()) writer.write(",");
            writer.write("\n");
        }
        writer.write("]");
        System.out.println("✅ Rapport exporté en JSON: " + path);
    } catch (IOException e) {
        System.err.println("❌ Erreur export JSON: " + e.getMessage());
    }
}

public static void exportCSV(String[] headers, Iterable<String[]> rows, String fileName) {
    ensureExportFolder();
    Path path = Path.of("exports", fileName);
    try (FileWriter writer = new FileWriter(path.toFile())) {
        writer.append(String.join(";", headers)).append("\n");
        for (String[] row : rows) {
            writer.append(String.join(";", row)).append("\n");
        }
        System.out.println("✅ Rapport exporté en CSV: " + path);
    } catch (IOException e) {
        System.err.println("❌ Erreur export CSV: " + e.getMessage());
    }
}


    public static void ensureExportFolder() {
        try {
            Files.createDirectories(Path.of("exports"));
        } catch (IOException e) {
            System.err.println("⚠️ Impossible de créer le dossier exports");
        }
    }
}

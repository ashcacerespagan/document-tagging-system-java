package capstone.documenttaggingsystem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for loading a trained IDF Map from a file.
 */
public class IdfLoader {

    /**
     * Opens an IDF Map file and parses it into a Map<String, Double>.
     * Normalizes file paths across OS platforms and trims input tokens.
     *
     * @param filepath The filepath to open.
     * @return The loaded IDF Map.
     */
    public Map<String, Double> loadMap(String filepath) {
        Map<String, Double> result = new HashMap<>();

        if (filepath == null || filepath.isBlank()) {
            System.out.println("Error: Provided filepath is null or empty.");
            return result;
        }

        // Normalize Windows backslashes to system-native file separators
        String normalizedPath = filepath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
        File file = new File(normalizedPath);

        if (!file.exists()) {
            System.out.println("Error loading idf map: File does not exist at " + file.getAbsolutePath());
            return result;
        }

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }

                String[] splitLine = line.split(",");
                if (splitLine.length != 2) {
                    System.out.println("Skipping invalid line (expected key,value): " + line);
                    continue;
                }

                String key = splitLine[0].trim();
                String valueStr = splitLine[1].trim();

                try {
                    result.put(key, Double.parseDouble(valueStr));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping line with invalid number: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading idf map: " + e.getMessage());
        }

        return result;
    }
}

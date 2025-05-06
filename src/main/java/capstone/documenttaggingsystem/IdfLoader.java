package capstone.documenttaggingsystem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This is a class for loading the trained IdfMap
 * from a file.
 */
public class IdfLoader {

    /**
     * Opens an Idf Map file and parses it into a Map<String, Double>
     *
     * @param filepath The filepath to open.
     * @return The loaded Idf Map.
     */
    public Map<String, Double> loadMap(String filepath){
        Map<String, Double> result = new HashMap<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filepath))){
            String line;
            while((line = bufferedReader.readLine()) != null) {
                String[] splitLine = line.split(",");
                if (splitLine.length != 2) {
                    System.out.println("Skipping invalid line (no key/value): " + line);
                    continue;
                }

                try {
                    result.put(splitLine[0], Double.parseDouble(splitLine[1]));
                } catch (NumberFormatException e) {
                    System.out.println("Skipping line with invalid number: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading idf map");
            System.out.println(e.getMessage());
        }

        return result;
    }
}

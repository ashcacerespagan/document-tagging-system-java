package capstone.documenttaggingsystem;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This is a class for opening a document, reading its entire
 * contents into a String, converting that String to lowercase,
 * and removing all non-alphanumeric characters and spaces from
 * the String.
 */
public class FileParser {

    /**
     * Opens the given filepath, reads the file into a String, replaces
     * tabs and newlines with spaces, then removes all non-alphanumeric
     * characters from it, and finally lowercases the String
     *
     * @param filepath The filepath to be read from
     * @return The parsed document as a String
     */
    public String convertFileToAlphanumericString(String filepath){

        try {
            String raw = readFileToString(filepath);

            // Empty file check
            if (raw == null || raw.trim().isEmpty()) {
                System.out.println("File is empty: " + filepath);
                return "";
            }

            return raw.replaceAll("[\\t\\n]", " ")
                    .replaceAll("[^a-zA-Z0-9 ]", "")
                    .toLowerCase()
                    .trim();

        } catch (Exception e){
            System.out.println("Error reading file from filepath: " + filepath);
            System.out.println(e.getMessage());
            return "";
        }
    }

    /**
     * Reads the contents of a file into a String
     *
     * @param filePath The filepath to read from
     * @return The document in String form
     * @throws IOException Error occurs when reading the file
     */
    public String readFileToString(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return new String(bytes, StandardCharsets.UTF_8);
    }
}

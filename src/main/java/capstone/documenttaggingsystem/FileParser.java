package capstone.documenttaggingsystem;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for reading document contents from disk and cleaning
 * raw text into a standardized, lowercase alphanumeric string for TF-IDF processing.
 */
public class FileParser {

    /**
     * Reads a file from disk, normalizes its path across operating systems,
     * replaces tabs/newlines with spaces, removes non-alphanumeric characters,
     * and lowercases the string.
     *
     * @param filepath The filepath to be read from
     * @return The cleaned alphanumeric document as a String, or empty string on error/empty file.
     */
    public String convertFileToAlphanumericString(String filepath) {
        if (filepath == null || filepath.isBlank()) {
            System.out.println("Error: Provided filepath is null or empty.");
            return "";
        }

        try {
            String raw = readFileToString(filepath);

            if (raw == null || raw.isBlank()) {
                System.out.println("File is empty: " + filepath);
                return "";
            }

            // Replace newlines/tabs with spaces, remove special characters, lower-case, collapse multi-spaces
            return raw.replaceAll("[\\r\\n\\t]", " ")
                    .replaceAll("[^a-zA-Z0-9 ]", "")
                    .toLowerCase()
                    .replaceAll("\\s+", " ")
                    .trim();

        } catch (Exception e) {
            System.out.println("Error reading file from filepath: " + filepath);
            System.out.println(e.getMessage());
            return "";
        }
    }

    /**
     * Reads the entire contents of a file into a UTF-8 String after normalizing path separators.
     *
     * @param filePath The filepath to read from
     * @return The raw document text
     * @throws IOException If an error occurs reading the file from disk
     */
    public String readFileToString(String filePath) throws IOException {
        if (filePath == null || filePath.isBlank()) {
            throw new IOException("Filepath cannot be null or empty.");
        }

        // Normalize Windows backslashes and Linux slashes
        String normalizedPath = filePath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
        Path path = Paths.get(normalizedPath);

        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + path.toAbsolutePath());
        }

        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes, StandardCharsets.UTF_8);
    }
}

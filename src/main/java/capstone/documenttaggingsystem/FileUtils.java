package capstone.documenttaggingsystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * Utility class for locating and collecting file paths from directory trees across operating systems.
 */
public class FileUtils {

    /**
     * Recursively retrieves absolute file paths for all .txt files in a directory and its subdirectories.
     *
     * @param directoryPath The root directory path to scan.
     * @return List of absolute file path strings for all matching .txt files.
     */
    public static List<String> getAllTxtFilePaths(String directoryPath) {
        if (directoryPath == null || directoryPath.isBlank()) {
            System.out.println("Error: Directory path is null or empty.");
            return Collections.emptyList();
        }

        // Normalize Windows backslashes and Linux slashes
        String normalizedPath = directoryPath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
        Path rootPath = Paths.get(normalizedPath);

        if (!Files.exists(rootPath) || !Files.isDirectory(rootPath)) {
            System.out.println("Specified directory does not exist or is not a directory: " + rootPath.toAbsolutePath());
            return Collections.emptyList();
        }

        List<String> filePaths = new ArrayList<>();

        try (Stream<Path> stream = Files.walk(rootPath)) {
            stream.filter(Files::isRegularFile)
                  .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".txt"))
                  .forEach(path -> filePaths.add(path.toAbsolutePath().toString()));
        } catch (IOException e) {
            System.out.println("Error traversing directory " + rootPath.toAbsolutePath() + ": " + e.getMessage());
        }

        return filePaths;
    }
}

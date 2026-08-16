package capstone.documenttaggingsystem;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Receives a directory filepath, opens every .txt file inside,
 * parses contents using FileParser, and compiles them into an IDF Map.
 */
@RequiredArgsConstructor
@Data
public class IdfTrainer {

    Map<String, Double> idfMap = new HashMap<>();

    final FileParser fileParser;

    // Cross-platform fallback directory
    final String DEFAULT_TRAINING_DIRECTORY = System.getProperty("user.dir") 
            + File.separator + "trainingData" 
            + File.separator + "testSet1" 
            + File.separator;

    /**
     * Trains an IDF Map given a directory for training data and a target
     * location to save the IDF Map.
     */
    public boolean createIdfMap(String trainingDirectoryFilepath, String targetSaveFilepath, boolean useStemming) {

        trainingDirectoryFilepath = (trainingDirectoryFilepath == null) 
                ? DEFAULT_TRAINING_DIRECTORY 
                : normalizePath(trainingDirectoryFilepath);

        if (!trainIdfMap(trainingDirectoryFilepath, useStemming)) {
            return false;
        }
        return saveIdfMap(targetSaveFilepath);
    }

    /**
     * Saves the class's trained IDF Map to the target file location.
     */
    private boolean saveIdfMap(String targetSaveFilepath) {
        if (targetSaveFilepath == null || targetSaveFilepath.isBlank()) {
            System.out.println("Error: Target save filepath is null or empty.");
            return false;
        }

        String normalizedSavePath = normalizePath(targetSaveFilepath);
        File file = new File(normalizedSavePath);

        if (file.exists()) {
            System.out.println("Specified IDF Map save filename already exists: " + file.getAbsolutePath());
            return false;
        }

        // Ensure parent directory exists before writing
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<String, Double> entry : idfMap.entrySet()) {
                bufferedWriter.write(entry.getKey() + "," + entry.getValue());
                bufferedWriter.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error saving idf map: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Trains the class's IDF Map on files in the specified directory.
     */
    public boolean trainIdfMap(String trainingDirectoryFilepath, boolean useStemming) {
        idfMap = new HashMap<>();
        double documentCount = 0;

        String normalizedDirPath = (trainingDirectoryFilepath == null) 
                ? DEFAULT_TRAINING_DIRECTORY 
                : normalizePath(trainingDirectoryFilepath);

        try {
            List<String> trainingFilePaths = FileUtils.getAllTxtFilePaths(normalizedDirPath);

            if (trainingFilePaths == null || trainingFilePaths.isEmpty()) {
                System.out.println("Specified training directory is empty or invalid: " + normalizedDirPath);
                return false;
            }

            for (String filePath : trainingFilePaths) {
                String normalizedFilePath = normalizePath(filePath);
                String parsedFile = fileParser.convertFileToAlphanumericString(normalizedFilePath);
                List<String> words;

                if (useStemming) {
                    words = WordStemmer.splitWithStemming(parsedFile);
                } else {
                    words = Arrays.stream(parsedFile.split("\\s+"))
                            .filter(s -> !s.isEmpty())
                            .toList();
                }

                Set<String> wordSet = new HashSet<>(words);

                for (String word : wordSet) {
                    idfMap.put(word, idfMap.getOrDefault(word, 0.0) + 1.0);
                }

                documentCount++;
            }

            if (documentCount == 0) {
                return false;
            }

            for (String word : idfMap.keySet()) {
                idfMap.put(word, Math.log(documentCount / idfMap.get(word)));
            }

        } catch (Exception e) {
            System.out.println("Error training the IDF Map: " + e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Normalizes path separators across OS platforms.
     */
    private String normalizePath(String path) {
        if (path == null) return null;
        return path.replace('\\', File.separatorChar).replace('/', File.separatorChar);
    }
}

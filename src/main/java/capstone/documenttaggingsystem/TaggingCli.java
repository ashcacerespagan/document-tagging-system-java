package capstone.documenttaggingsystem;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;

/**
 * Command-Line Interface (CLI) for managing document tagging operations,
 * IDF map training, and keyword extraction routines.
 */
public class TaggingCli {

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.println("""
                    =======================================
                    🧠 Document Tagging System CLI
                    =======================================
                    Please choose from the following:
                    1: Extract Keywords
                    2: Train IDF Map
                    3: Exit
                    """);

            System.out.print("Select an option (1-3): ");
            String choice = input.nextLine().trim();

            switch (choice) {
                case "1" -> extractKeywords(input);
                case "2" -> trainIdf(input);
                case "3" -> {
                    System.out.println("\nThank you for using the Document Tagging Application!");
                    input.close();
                    System.exit(0);
                }
                default -> System.out.println("\n❌ Invalid option. Please select 1, 2, or 3.\n");
            }
        }
    }

    /**
     * Handles the keyword extraction workflow.
     *
     * @param input Shared Scanner instance.
     */
    static void extractKeywords(Scanner input) {
        TfIdfCalculator tfIdfCalculator = new TfIdfCalculator(new IdfLoader());
        FileParser fileParser = new FileParser();

        String inputFilepath;
        String idfMapFilepath;
        int keywordCount;
        boolean useStemming;

        String workingDir = System.getProperty("user.dir");

        // Get relative path to input document
        while (true) {
            System.out.println("\nEnter relative input document path (or press Enter to cancel):");
            System.out.println("Working Directory: " + workingDir);
            System.out.print("Path: ");
            String relativePath = input.nextLine().trim();

            if (relativePath.isEmpty()) {
                return;
            }

            inputFilepath = resolvePath(workingDir, relativePath);
            if (new File(inputFilepath).exists()) {
                break;
            } else {
                System.out.println("❌ File not found at " + inputFilepath + ". Please try again.");
            }
        }

        // Get relative path to IDF map file (Optional - blank uses default)
        System.out.println("\nEnter relative path for custom IDF Map (or press Enter for default map):");
        System.out.println("Working Directory: " + workingDir);
        System.out.print("Path: ");
        String customIdfRelative = input.nextLine().trim();

        if (customIdfRelative.isEmpty()) {
            idfMapFilepath = null;
        } else {
            idfMapFilepath = resolvePath(workingDir, customIdfRelative);
        }

        // Ask for keyword count
        while (true) {
            System.out.print("\nEnter keyword count (integer 1–20): ");
            try {
                keywordCount = Integer.parseInt(input.nextLine().trim());
                if (keywordCount >= 1 && keywordCount <= 20) {
                    break;
                }
                System.out.println("⚠️ Please enter a number between 1 and 20.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid integer input. Try again.");
            }
        }

        // Ask about stemming
        useStemming = isUseStemming(input);

        // Process file and print results
        System.out.println("\n⏳ Extracting keywords...");
        String parsedContent = fileParser.convertFileToAlphanumericString(inputFilepath);

        if (parsedContent.isBlank()) {
            System.out.println("❌ Could not read or parse document content.");
            return;
        }

        Map<String, Double> topKeywords = tfIdfCalculator.getTopKeywords(
                parsedContent,
                idfMapFilepath,
                keywordCount,
                useStemming
        );

        System.out.println("\n=======================================");
        System.out.println("RESULTS: Top Keywords");
        System.out.println("=======================================");
        if (topKeywords.isEmpty()) {
            System.out.println("No keywords extracted.");
        } else {
            topKeywords.forEach((key, value) -> 
                System.out.printf("%-18s — %.4f%n", key, value)
            );
        }
        System.out.println("=======================================\n");
    }

    /**
     * Handles training an IDF map from a folder of training documents.
     *
     * @param input Shared Scanner instance.
     */
    static void trainIdf(Scanner input) {
        IdfTrainer idfTrainer = new IdfTrainer(new FileParser());

        String trainingDirectoryFilepath;
        String targetSaveFilepath;
        String workingDir = System.getProperty("user.dir");

        // Path to training data folder
        while (true) {
            System.out.println("\nEnter training directory relative path:");
            System.out.println("Working Directory: " + workingDir);
            System.out.print("Path: ");
            String relativeDir = input.nextLine().trim();

            if (relativeDir.isEmpty()) {
                return;
            }

            trainingDirectoryFilepath = resolvePath(workingDir, relativeDir);
            File dir = new File(trainingDirectoryFilepath);
            if (dir.exists() && dir.isDirectory()) {
                break;
            } else {
                System.out.println("❌ Directory not found at " + trainingDirectoryFilepath);
            }
        }

        // Path to save target IDF map
        while (true) {
            System.out.println("\nEnter relative destination filepath to save new IDF Map:");
            System.out.println("Working Directory: " + workingDir);
            System.out.print("Path: ");
            String relativeSave = input.nextLine().trim();

            if (relativeSave.isEmpty()) {
                return;
            }

            targetSaveFilepath = resolvePath(workingDir, relativeSave);
            break;
        }

        boolean useStemming = isUseStemming(input);

        System.out.println("\n⏳ Training IDF Map...");
        if (idfTrainer.createIdfMap(trainingDirectoryFilepath, targetSaveFilepath, useStemming)) {
            System.out.println("✅ IDF Map trained and saved to: " + targetSaveFilepath + "\n");
        } else {
            System.out.println("❌ Error creating IDF Map.\n");
        }
    }

    private static boolean isUseStemming(Scanner input) {
        while (true) {
            System.out.print("\nEnable stemming? (Y/N): ");
            String answer = input.nextLine().trim();

            if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                return true;
            } else if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) {
                return false;
            }
            System.out.println("⚠️ Please enter Y or N.");
        }
    }

    private static String resolvePath(String baseDir, String relativePath) {
        Path resolved = Paths.get(baseDir, relativePath);
        return resolved.toAbsolutePath().normalize().toString();
    }
}

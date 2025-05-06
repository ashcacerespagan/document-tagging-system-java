package capstone.documenttaggingsystem;

import java.util.Map;
import java.util.Scanner;

/**
 * This is the CLI for managing file selection for the
 * document tagging process. Meant for console-based use.
 */
public class TaggingCli {
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);

        while(true){
            System.out.println("""
                    Please choose from the following:
                    1: Extract Keywords
                    2: Train IDF Map
                    3: Exit""");

            String choice = input.next();

            switch(choice){
                case "1" -> extractKeywords(); // Starts keyword extraction routine
                case "2" -> trainIdf(); // Starts IDF training routine
                case "3" -> {
                    System.out.println("Thank you for using the Document Tagging Application!");
                    input.close(); // Clean up scanner on exit
                    System.exit(0);
                }
                default -> System.out.println("Please select option 1, 2, or 3");
            }
        }
    }

    /**
     * Handles the keyword extraction flow using user input.
     * Grabs a file, an IDF map, and keyword count, then prints results.
     */
    static void extractKeywords(){
        TfIdfCalculator tfIdfCalculator = new TfIdfCalculator(new IdfLoader());
        FileParser fileParser = new FileParser();

        String inputFilepath;
        String idfMapFilepath;
        int keywordCount;
        boolean useStemming;

        Scanner input = new Scanner(System.in);
        String workingDir = System.getProperty("user.dir");

        // Get user path to input file
        do {
            System.out.println("Please enter the input file's filepath from current directory.");
            System.out.println("Current directory is: " + workingDir);

            inputFilepath = workingDir + input.next();

        } while (inputFilepath == null || inputFilepath.isEmpty());

        // Get path to IDF map file
        do {
            System.out.println("Please enter the filepath for the IDF Map to use from current directory.");
            System.out.println("Current directory is: " + workingDir);

            idfMapFilepath = workingDir + input.next();

        } while (idfMapFilepath == null || idfMapFilepath.isEmpty());

        // Ask for keyword count
        while(true){
            System.out.println("Please enter the number of keywords you would like as an integer from 1–20.");

            try{
                keywordCount = input.nextInt();
                break;
            } catch(Exception e) {
                System.out.println("Error parsing your input. Please use an integer from 1–20.");
                input.nextLine(); // clear invalid token
            }
        }

        // Ask about stemming toggle
        useStemming = isUseStemming(input);

        // Extract and display keywords from file
        Map<String, Double> topKeywords = tfIdfCalculator.getTopKeywords(
                fileParser.convertFileToAlphanumericString(inputFilepath),
                idfMapFilepath,
                keywordCount,
                useStemming
        );

        // Output result
        System.out.println("Top keywords are:");
        topKeywords.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

        input.close();
    }

    private static boolean isUseStemming(Scanner input) {
        boolean useStemming;
        while(true){
            System.out.println("Would you like to use stemming? Y/N");

            String answer = input.next();

            if(answer.equalsIgnoreCase("y")) {
                useStemming = true;
                break;
            } else if (answer.equalsIgnoreCase("n")){
                useStemming = false;
                break;
            }
        }
        return useStemming;
    }

    /**
     * Handles training the IDF map using a folder of training data.
     * Prompts user for folder location and save destination.
     */
    static void trainIdf(){
        IdfTrainer idfTrainer = new IdfTrainer(new FileParser());

        String trainingDirectoryFilepath;
        String targetSaveFilepath;

        Scanner input = new Scanner(System.in);
        String workingDir = System.getProperty("user.dir");

        // Path to training data folder
        do {
            System.out.println("Please enter the training directory filepath from current directory.");
            System.out.println("Current directory is: " + workingDir);

            trainingDirectoryFilepath = workingDir + input.next();

        } while (trainingDirectoryFilepath == null || trainingDirectoryFilepath.isEmpty());

        // Path to save IDF map
        do {
            System.out.println("Please enter the filepath for the IDF Map to use from current directory.");
            System.out.println("Current directory is: " + workingDir);

            targetSaveFilepath = workingDir + input.next();

        } while (targetSaveFilepath == null || targetSaveFilepath.isEmpty());

        // Ask whether stemming should be applied
        boolean useStemming = isUseStemming(input);

        // Attempt to train the IDF model and save it
        if(idfTrainer.createIdfMap(trainingDirectoryFilepath, targetSaveFilepath, useStemming)){
            System.out.println("IDF Map trained and saved to " + targetSaveFilepath);
        } else {
            System.out.println("Error creating IDF Map");
        }

        input.close();
    }
}

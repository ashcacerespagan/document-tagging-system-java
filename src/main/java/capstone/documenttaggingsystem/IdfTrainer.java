package capstone.documenttaggingsystem;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * This class will receive a filepath directory, and using that, it will
 * open every .txt file in the directory, parse its contents using the
 * FileParser class, and combine all entries into an Idf Map.
 */
@RequiredArgsConstructor
@Data
public class IdfTrainer {

    Map<String, Double> idfMap = new HashMap<>();

    final FileParser fileParser;

    // fallback directory if training path is null
    final String DEFAULT_TRAINING_DIRECTORY = System.getProperty("user.dir") + "\\trainingData\\testSet1\\";

    /**
     * Trains an Idf Map given a directory for training data and a target
     * location to save the Idf Map.
     */
    public boolean createIdfMap(String trainingDirectoryFilepath, String targetSaveFilepath, boolean useStemming){

        // default path if none provided
        trainingDirectoryFilepath = trainingDirectoryFilepath == null ? DEFAULT_TRAINING_DIRECTORY : trainingDirectoryFilepath;

        if(!trainIdfMap(trainingDirectoryFilepath, useStemming)){
            return false;
        }
        return saveIdfMap(targetSaveFilepath);
    }

    /**
     * Saves the class's trained Idf Map to the target file location.
     */
    private boolean saveIdfMap(String targetSaveFilepath){

        // Verify the target filepath isn't already in use
        File file = new File(targetSaveFilepath);

        if(file.exists()){
            System.out.println("Specified Idf Map save filename already exists");
            return false;
        }

        // Save each entry in the Idf Map to a new line in target file,
        // separating key and value by comma
        try(BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(targetSaveFilepath))){
            for (Map.Entry<String, Double> entry : idfMap.entrySet()){
                bufferedWriter.write(entry.getKey() + "," + entry.getValue());
                bufferedWriter.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error saving idf map");
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Trains the class's Idf Map on the files in the specified training
     * directory.
     */
    public boolean trainIdfMap(String trainingDirectoryFilepath, boolean useStemming){
        idfMap = new HashMap<>();
        double documentCount = 0;

        try{
            List<String> trainingFilePaths = FileUtils.getAllTxtFilePaths(trainingDirectoryFilepath);

            if (trainingFilePaths.isEmpty()) {
                System.out.println("Specified training directory is empty");
                return false;
            }

            for (String filePath : trainingFilePaths) {
                String parsedFile = fileParser.convertFileToAlphanumericString(filePath);
                List<String> words;

                // use stemmed word split if enabled
                if(useStemming){
                    words = WordStemmer.splitWithStemming(parsedFile);
                } else {
                    words  = Arrays.stream(parsedFile.split(" ")).toList();
                }

                // make a unique set of words from the doc
                Set<String> wordSet = new HashSet<>(words);

                // count how many docs contain each word
                for(String word : wordSet){
                    idfMap.put(word, idfMap.getOrDefault(word, 0.0) + 1);
                }

                documentCount++;
            }

            // convert raw counts into IDF scores
            for(String word : idfMap.keySet()){
                idfMap.put(word, Math.log(documentCount / idfMap.get(word)));
            }

        } catch (Exception e) {
            System.out.println("Error training the Idf Map");
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }
}

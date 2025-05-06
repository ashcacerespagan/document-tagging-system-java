package capstone.documenttaggingsystem;

import lombok.AllArgsConstructor;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This is a class which will receive a parsed text file as a String,
 * then run the Term Frequency algorithm over it and compare it with
 * a saved Inverse Document Frequency map to produce the combined
 * Tf-Idf values, which it will then use to return the top 10 recommended
 * tags.
 */
@AllArgsConstructor
public class TfIdfCalculator {

    final String DEFAULT_IDF_MAP = System.getProperty("user.dir") + File.separator + "idfMaps" + File.separator + "testIdfMap.txt";

    IdfLoader idfLoader;

    /**
     * Retrieves the keyword map for a given input document and an Idf Map, then returns the top
     * results sorted by the weights in the keyword map.
     *
     * @param inputDocument The input document to get keywords from.
     * @param idfMapFilepath The filepath of the Idf Map to use. Leaving this null will result in the default
     *                       Idf Map.
     * @param numberOfResults How many top keywords should be returned.
     * @return A LinkedHashMap<String, Double> of the sorted top keywords and their weights.
     */
    public Map<String, Double> getTopKeywords(String inputDocument, String idfMapFilepath,
                                              int numberOfResults, boolean useStemming){
        Map<String, Double> keywords = getKeywords(inputDocument, idfMapFilepath, useStemming);

        if(numberOfResults > 20) {
            numberOfResults = 20;
        }

        if(numberOfResults < 1) {
            numberOfResults = 1;
        }

        // Tracks top keywords using a min-heap based on tf-idf score
        PriorityQueue<Map.Entry<String, Double>> topKeywords = new PriorityQueue<>(
                Comparator.comparingDouble(Map.Entry::getValue)
        );

        for(Map.Entry<String, Double> entry : keywords.entrySet()){
            topKeywords.offer(entry);
            if(topKeywords.size() > numberOfResults){
                topKeywords.poll();
            }
        }

        // Reverse the heap into a sorted map for output
        return topKeywords.stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * Gets the keyword map for an input document and an Idf model to test
     * against.
     *
     * @param inputDocument The document to use for Tf
     * @param idfMapFilepath The map to use for Idf. Leave null for default.
     * @return The map of keywords and their weights.
     */
    public Map<String, Double> getKeywords(String inputDocument, String idfMapFilepath, boolean useStemming){
        idfMapFilepath = Objects.requireNonNullElse(idfMapFilepath, DEFAULT_IDF_MAP);

        Map<String, Double> tfMap = getTfMap(inputDocument, useStemming);
        Map<String, Double> idfMap = idfLoader.loadMap(idfMapFilepath);

        // Finds max IDF value to apply when a word isn't in the IDF Map
        double max = 1.0;
        for(String word : idfMap.keySet()){
            if(idfMap.get(word) > max){
                max = idfMap.get(word);
            }
        }

        Map<String, Double> result = new HashMap<>();

        // Apply tf * idf logic for all terms
        for(String word : tfMap.keySet()){
            result.put(word, tfMap.get(word) * idfMap.getOrDefault(word, max + 1));
        }

        return result;
    }

    /**
     * Generates a Tf Map for the given input document.
     *
     * @param inputDocument The document to be Tf Mapped.
     * @return The Tf Map of the input document.
     */
    public Map<String, Double> getTfMap(String inputDocument, boolean useStemming){
        Map<String, Double> result = new HashMap<>();

        if (inputDocument == null || inputDocument.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> splitInput;

        // split input using stemming or plain whitespace
        if(useStemming){
            splitInput = WordStemmer.splitWithStemming(inputDocument);
        } else {
            splitInput = Arrays.stream(inputDocument.split(" ")).toList();
        }

        int wordCount = splitInput.size();

        // skip tf logic if the doc has no words
        if (wordCount == 0) {
            return result;
        }

        for(String word : splitInput){
            result.put(word, result.getOrDefault(word, 0.0) + 1);
        }

        // divide term counts by total words to get TF values
        result.replaceAll((k, v) -> result.get(k) / wordCount);

        return result;
    }
}

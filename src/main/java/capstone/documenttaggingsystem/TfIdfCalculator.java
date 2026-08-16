package capstone.documenttaggingsystem;

import lombok.AllArgsConstructor;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class which receives a parsed text file as a String,
 * runs the Term Frequency (TF) algorithm over it, and compares it with
 * a saved Inverse Document Frequency (IDF) map to produce combined
 * TF-IDF weights to return top recommended keywords/tags.
 */
public class TfIdfCalculator {

    private final String defaultIdfMap;
    private final IdfLoader idfLoader;

    /**
     * Default constructor for production usage.
     * Sets the default IDF map path based on the working directory.
     *
     * @param idfLoader The IdfLoader instance to use.
     */
    public TfIdfCalculator(IdfLoader idfLoader) {
        this(
            System.getProperty("user.dir") + File.separator + "idfMaps" + File.separator + "testIdfMap.txt",
            idfLoader
        );
    }

    /**
     * Fully parameterized constructor allowing tests to override default map paths.
     * Guards against null/blank paths by falling back to the default working directory path.
     *
     * @param defaultIdfMap Custom path to default IDF map.
     * @param idfLoader The IdfLoader instance to use.
     */
    public TfIdfCalculator(String defaultIdfMap, IdfLoader idfLoader) {
        String fallbackPath = System.getProperty("user.dir") + File.separator + "idfMaps" + File.separator + "testIdfMap.txt";
        String pathToUse = (defaultIdfMap == null || defaultIdfMap.isBlank()) ? fallbackPath : defaultIdfMap;
        this.defaultIdfMap = normalizePath(pathToUse);
        this.idfLoader = idfLoader;
    }

    /**
     * Retrieves the keyword map for a given input document and an IDF Map, then returns the top
     * results sorted by the weights in the keyword map.
     *
     * @param inputDocument The input document to get keywords from.
     * @param idfMapFilepath The filepath of the IDF Map to use. Leaving this null uses default.
     * @param numberOfResults How many top keywords should be returned (capped between 1 and 20).
     * @param useStemming Whether to apply stemmer during tokenization.
     * @return A LinkedHashMap of the sorted top keywords and their weights.
     */
    public Map<String, Double> getTopKeywords(String inputDocument, String idfMapFilepath,
                                              int numberOfResults, boolean useStemming) {
        Map<String, Double> keywords = getKeywords(inputDocument, idfMapFilepath, useStemming);

        if (numberOfResults > 20) {
            numberOfResults = 20;
        }

        if (numberOfResults < 1) {
            numberOfResults = 1;
        }

        // Tracks top keywords using a min-heap based on tf-idf score
        PriorityQueue<Map.Entry<String, Double>> topKeywords = new PriorityQueue<>(
                Comparator.comparingDouble(Map.Entry::getValue)
        );

        for (Map.Entry<String, Double> entry : keywords.entrySet()) {
            topKeywords.offer(entry);
            if (topKeywords.size() > numberOfResults) {
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
     * Gets the keyword map for an input document and an IDF model.
     *
     * @param inputDocument The document to use for TF calculation.
     * @param idfMapFilepath The map to use for IDF. Leave null for default.
     * @param useStemming Whether to stem words before calculating TF.
     * @return The map of keywords and their combined TF-IDF weights.
     */
    public Map<String, Double> getKeywords(String inputDocument, String idfMapFilepath, boolean useStemming) {
        String targetPath = (idfMapFilepath == null) ? defaultIdfMap : normalizePath(idfMapFilepath);

        Map<String, Double> tfMap = getTfMap(inputDocument, useStemming);
        Map<String, Double> idfMap = idfLoader.loadMap(targetPath);

        // Finds max IDF value to apply when a word isn't in the IDF Map
        double max = 1.0;
        if (idfMap != null && !idfMap.isEmpty()) {
            for (Double value : idfMap.values()) {
                if (value > max) {
                    max = value;
                }
            }
        }

        Map<String, Double> result = new HashMap<>();

        // Apply tf * idf logic for all terms
        for (Map.Entry<String, Double> entry : tfMap.entrySet()) {
            String word = entry.getKey();
            Double tfValue = entry.getValue();
            double idfValue = (idfMap != null) ? idfMap.getOrDefault(word, max + 1.0) : max + 1.0;
            result.put(word, tfValue * idfValue);
        }

        return result;
    }

    /**
     * Generates a Term Frequency (TF) Map for the given input document.
     *
     * @param inputDocument The document to be TF mapped.
     * @param useStemming Whether to stem words during tokenization.
     * @return The TF Map of the input document.
     */
    public Map<String, Double> getTfMap(String inputDocument, boolean useStemming) {
        if (inputDocument == null || inputDocument.trim().isEmpty()) {
            return Collections.emptyMap();
        }

        List<String> splitInput;

        // Split input using stemming or plain whitespace
        if (useStemming) {
            splitInput = WordStemmer.splitWithStemming(inputDocument);
        } else {
            splitInput = Arrays.stream(inputDocument.split("\\s+"))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }

        int wordCount = splitInput.size();

        if (wordCount == 0) {
            return Collections.emptyMap();
        }

        Map<String, Double> result = new HashMap<>();
        for (String word : splitInput) {
            result.put(word, result.getOrDefault(word, 0.0) + 1.0);
        }

        // Divide term counts by total word count to obtain normalized TF values
        result.replaceAll((k, v) -> v / wordCount);

        return result;
    }

    /**
     * Normalizes path separators across Linux and Windows.
     */
    private String normalizePath(String path) {
        if (path == null) return null;
        return path.replace('\\', File.separatorChar).replace('/', File.separatorChar);
    }
}

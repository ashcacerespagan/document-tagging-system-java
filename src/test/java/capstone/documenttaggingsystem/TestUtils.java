package capstone.documenttaggingsystem;

import org.mockito.Mockito;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility fixture class providing standardized mock documents, expected IDF maps,
 * and dev helpers for the Document Tagging System.
 */
public class TestUtils {

    /**
     * Generates mocked test documents for term frequency (TF) and IDF calculations.
     *
     * @return An unmodifiable list of test document strings.
     */
    public static List<String> generateTestStrings() {
        return List.of(
                "this is for testing purposes",
                "this this is for testing",
                "just a testing string"
        );
    }

    /**
     * Generates expected IDF map based on the 3 mock documents in generateTestStrings().
     * Score formula: ln(Total Documents / Document Frequency)
     *
     * @return An unmodifiable testing IDF map with expected weights.
     */
    public static Map<String, Double> generateIdfMap() {
        Map<String, Double> result = new HashMap<>();

        // Document count N = 3
        result.put("this", Math.log(3.0 / 2.0));
        result.put("is", Math.log(3.0 / 2.0));
        result.put("for", Math.log(3.0 / 2.0));
        result.put("testing", Math.log(3.0 / 3.0)); // ln(1.0) = 0.0
        result.put("purposes", Math.log(3.0 / 1.0));
        result.put("just", Math.log(3.0 / 1.0));
        result.put("a", Math.log(3.0 / 1.0));
        result.put("string", Math.log(3.0 / 1.0));

        return Collections.unmodifiableMap(result);
    }

    /**
     * Dev utility method to regenerate testIdfMap.txt on disk using mocked test strings.
     *
     * @return true if the IDF map was successfully trained and written to disk.
     */
    public static boolean generateTestIdfMapFileOnDisk() {
        FileParser mockFileParser = Mockito.mock(FileParser.class);
        IdfTrainer trainer = new IdfTrainer(mockFileParser);

        List<String> testStrings = generateTestStrings();

        Mockito.when(mockFileParser.convertFileToAlphanumericString(Mockito.anyString()))
                .thenReturn(testStrings.get(0))
                .thenReturn(testStrings.get(1))
                .thenReturn(testStrings.get(2));

        String trainingDir = System.getProperty("user.dir")
                + File.separator + "trainingData"
                + File.separator + "testSet1"
                + File.separator;

        String targetSavePath = System.getProperty("user.dir")
                + File.separator + "idfMaps"
                + File.separator + "testIdfMap.txt";

        File outputFile = new File(targetSavePath);
        if (outputFile.exists()) {
            outputFile.delete();
        }

        return trainer.createIdfMap(trainingDir, targetSavePath, false);
    }
}

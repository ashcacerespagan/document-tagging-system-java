package capstone.documenttaggingsystem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Map;

/**
 * Integration test suite verifying end-to-end document parsing and TF-IDF keyword extraction.
 */
public class BackendIntegrationTest {

    private final FileParser fileParser = new FileParser();
    private final TfIdfCalculator tfIdfCalculator = new TfIdfCalculator(new IdfLoader());

    @Test
    public void testFullKeywordExtractionFromFile() {
        String testDocPath = System.getProperty("user.dir") 
                + File.separator + "testDocuments" 
                + File.separator + "testDocument1.txt";

        String idfMapFilepath = System.getProperty("user.dir") 
                + File.separator + "idfMaps" 
                + File.separator + "testIdfMap.txt";

        String inputDocument = fileParser.convertFileToAlphanumericString(testDocPath);

        Map<String, Double> result = tfIdfCalculator.getTopKeywords(
                inputDocument,
                idfMapFilepath,
                7,
                false
        );

        // Asserts keywords are extracted properly
        Assertions.assertFalse(result.isEmpty(), "Extracted keywords map should not be empty");
        Assertions.assertEquals(6, result.size(), "Should return exactly 6 extracted keywords");

        // Asserts 'only' is the top keyword and 'purposes' is the second-highest keyword
        Object[] topKeys = result.keySet().toArray();
        Assertions.assertEquals("only", topKeys[0]);
        Assertions.assertEquals("purposes", topKeys[1]);
    }

    @Test
    public void testLargeFileProcessing() {
        String largeDocPath = System.getProperty("user.dir") 
                + File.separator + "testDocuments" 
                + File.separator + "large" 
                + File.separator + "testLarge.txt";

        String idfMapFilepath = System.getProperty("user.dir") 
                + File.separator + "idfMaps" 
                + File.separator + "testIdfMap.txt";

        String inputDocument = fileParser.convertFileToAlphanumericString(largeDocPath);

        Map<String, Double> result = tfIdfCalculator.getTopKeywords(inputDocument, idfMapFilepath, 10, false);

        Assertions.assertFalse(result.isEmpty(), "Large file processing should extract keywords");
    }

    @Test
    public void testGarbageInputFile() {
        String symbolsDocPath = System.getProperty("user.dir") 
                + File.separator + "testDocuments" 
                + File.separator + "parser" 
                + File.separator + "testOnlySymbols.txt";

        String idfMapFilepath = System.getProperty("user.dir") 
                + File.separator + "idfMaps" 
                + File.separator + "testIdfMap.txt";

        String inputDocument = fileParser.convertFileToAlphanumericString(symbolsDocPath);

        Map<String, Double> result = tfIdfCalculator.getTopKeywords(inputDocument, idfMapFilepath, 5, false);

        // Expect no keywords since symbols are cleaned away to an empty string
        Assertions.assertTrue(result.isEmpty(), "Garbage/symbols-only file should return an empty keyword map");
    }
}

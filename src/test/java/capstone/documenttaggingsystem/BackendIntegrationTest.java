package capstone.documenttaggingsystem;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class BackendIntegrationTest {

    FileParser fileParser = new FileParser();

    TfIdfCalculator tfIdfCalculator = new TfIdfCalculator(new IdfLoader());

    @Test
    public void testFullKeywordExtractionFromFile(){
        String inputDocument = fileParser.convertFileToAlphanumericString(
                System.getProperty("user.dir") + "\\testDocuments\\testDocument1.txt\\");
        String idfMapFilepath = System.getProperty("user.dir") + "\\idfMaps\\testIdfMap.txt";

        Map<String, Double> result = tfIdfCalculator.getTopKeywords(
                inputDocument,
                idfMapFilepath,
                7,
                false
        );

        //For manually verifying map values
//        result.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

        //Asserts all 6 keywords are returned, but that no extra 7th entry is added
        assert(result.size() == 6);

        //Asserts the unique word "only" is the top keyword and that the rare word
        //"purposes" is the second-highest keyword
        assert(result.keySet().toArray()[0].equals("only"));
        assert(result.keySet().toArray()[1].equals("purposes"));
    }
    // Verifies the system can handle a large document without crashing.
    @Test
    public void testLargeFileProcessing() {
        String inputDocument = fileParser.convertFileToAlphanumericString(
                System.getProperty("user.dir") + "/testDocuments/large/testLarge.txt"
        );

        String idfMapFilepath = System.getProperty("user.dir") + "\\idfMaps\\testIdfMap.txt";

        Map<String, Double> result = tfIdfCalculator.getTopKeywords(inputDocument, idfMapFilepath, 10, false);

        // Should return something if input has meaningful words
        assert(!result.isEmpty());
    }

    // Verifies the system handles garbage/nonsense input.
    @Test
    public void testGarbageInputFile() {
        String inputDocument = fileParser.convertFileToAlphanumericString(
                System.getProperty("user.dir") + "/testDocuments/parser/testOnlySymbols.txt"
        );

        String idfMapFilepath = System.getProperty("user.dir") + "\\idfMaps\\testIdfMap.txt";

        Map<String, Double> result = tfIdfCalculator.getTopKeywords(inputDocument, idfMapFilepath, 5, false);

        // Expect no keywords since the input was cleaned to an empty string
        assert(result.isEmpty());
    }
}

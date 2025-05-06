package capstone.documenttaggingsystem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class TfIdfCalculatorTest {

    @Mock
    IdfLoader idfLoader;


    @InjectMocks
    TfIdfCalculator tfIdfCalculator;

    /**
     * Verifies that getTfMap generates a correctly weighted tf map.
     */
    @Test
    public void getTfMapHappyPath(){
        List<String> inputs = TestUtils.generateTestStrings();

        Map<String, Double> result0 = tfIdfCalculator.getTfMap(inputs.get(0), false);
        Map<String, Double> result1 = tfIdfCalculator.getTfMap(inputs.get(1), false);
        Map<String, Double> result2 = tfIdfCalculator.getTfMap(inputs.get(2), false);

        Assertions.assertEquals(0.2, result0.get("this"));
        Assertions.assertEquals(0.4, result1.get("this"));
        Assertions.assertNull(result2.get("this"));
    }

    /**
     * Verifies that getKeywords generates a correctly weighted keyword map.
     */
    @Test
    public void getKeywordsHappyPath(){
        Mockito.when(idfLoader.loadMap(Mockito.anyString()))
                .thenReturn(TestUtils.generateIdfMap());

        List<String> inputs = TestUtils.generateTestStrings();

        Map<String, Double> result0 = tfIdfCalculator.getKeywords(inputs.get(0), "anything", false);
        Map<String, Double> result1 = tfIdfCalculator.getKeywords(inputs.get(1), "anything", false);
        Map<String, Double> result2 = tfIdfCalculator.getKeywords(inputs.get(2), "anything", false);

        //For manually verifying map values
//        result0.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
//        result1.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
//        result2.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

        //Verifies "purposes" is the highest rated keyword in string 0
        assert (result0.get("purposes") > result0.get("this"));
        assert (result0.get("purposes") > result0.get("is"));
        assert (result0.get("purposes") > result0.get("for"));
        assert (result0.get("purposes") > result0.get("testing"));

        //Verifies "this" is the highest rated keyword in string 1
        assert (result1.get("this") > result1.get("is"));
        assert (result1.get("this") > result1.get("for"));
        assert (result1.get("this") > result1.get("testing"));

        //Verifies "testing" is the lowest rated keyword in string 2
        assert (result2.get("testing") < result2.get("just"));
        assert (result2.get("testing") < result2.get("a"));
        assert (result2.get("testing") < result2.get("string"));
    }

    /**
     * Verifies that the getKeywords method uses the default idfMapFilepath when
     * given a null argument.
     */
    @Test
    public void getKeywordsDefaultTest(){
        String defaultMapLoc = System.getProperty("user.dir") + "\\idfMaps\\testIdfMap.txt";

        tfIdfCalculator.getKeywords("test", null, false);

        Mockito.verify(idfLoader).loadMap(defaultMapLoc);
    }

    /**
     * Verifies that the getKeywords method uses the input idfMapFilepath when
     * given one.
     */
    @Test
    public void getKeywordsNonDefaultTest(){
        String input = "filler";

        tfIdfCalculator.getKeywords("test", input, false);

        Mockito.verify(idfLoader).loadMap(input);
    }

    /**
     * Verifies that getTopKeywords correctly generates a weighted list of the top keywords.
     */
    @Test
    public void getTopKeywordsHappyPath(){
        Mockito.when(idfLoader.loadMap(Mockito.anyString()))
                .thenReturn(TestUtils.generateIdfMap());

        List<String> inputs = TestUtils.generateTestStrings();

        Map<String, Double> result0 = tfIdfCalculator.getTopKeywords(inputs.get(0), "anything", 3, false);
        Map<String, Double> result1 = tfIdfCalculator.getTopKeywords(inputs.get(1), "anything", 3, false);
        Map<String, Double> result2 = tfIdfCalculator.getTopKeywords(inputs.get(2), "anything", 3, false);

        //For manually verifying map values
//        result0.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
//        result1.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));
//        result2.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

        //Verifies that "purposes" is the highest rated keyword in string 0
        assert (result0.keySet().toArray()[0].equals("purposes"));

        //Verifies that "this" is the highest rated keyword in string 1
        assert (result1.keySet().toArray()[0].equals("this"));

        //Verifies that "testing" is the lowest rated keyword in string 2 and that result length is 3
        assert (!result2.containsKey("testing"));
    }

    /**
     * Verifies that default idf value (that is, a word not appearing in the map) has
     * a value higher than the highest value in the Idf Map.
     */
    @Test
    public void testDefaultIdfValue(){
        Mockito.when(idfLoader.loadMap(Mockito.anyString()))
                .thenReturn(TestUtils.generateIdfMap());

        String input = "not a";

        Map<String, Double> result = tfIdfCalculator.getKeywords(input, "anything", false);

        assert ( result.get("not") > result.get("a"));
    }
    // Verifies that getTopKeywords enforces a max of 20 keywords even if a higher number is requested.
    @Test
    public void testTopKeywordsMaxLimit() {
        Mockito.when(idfLoader.loadMap(Mockito.anyString()))
                .thenReturn(TestUtils.generateIdfMap());

        String input = "this is for testing purposes only just a string";
        Map<String, Double> result = tfIdfCalculator.getTopKeywords(input, "dummy", 999, false);

        assert(result.size() <= 20);
    }

    // Verifies that getTopKeywords enforces a minimum of 1 keyword even if 0 is requested.
    @Test
    public void testTopKeywordsMinLimit() {
        Mockito.when(idfLoader.loadMap(Mockito.anyString()))
                .thenReturn(TestUtils.generateIdfMap());

        String input = "this is a short file";
        Map<String, Double> result = tfIdfCalculator.getTopKeywords(input, "dummy", 0, false);

        assert(result.size() == 1);
    }

    // Verifies that loading from a missing IDF file doesn't crash and returns fallback keywords.

    @Test
    public void testMissingIdfMapFallback() {
        String input = "this is a document with random words";

        // Use a clearly fake path to simulate missing file
        Map<String, Double> result = tfIdfCalculator.getTopKeywords(input, "missing_idf_file.txt", 5, false);

        // We expect results anyway, with fallback IDF values
        assert(!result.isEmpty());
    }
    @Test
    public void testEmptyInputReturnsEmptyKeywordMap() {
        Map<String, Double> result = tfIdfCalculator.getTopKeywords("", null, 5, false);
        assert(result.isEmpty());
    }


}
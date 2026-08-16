package capstone.documenttaggingsystem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Unit test suite for verifying WordStemmer tokenization and Porter Stemming operations.
 */
public class WordStemmerTest {

    // Verifies that the stemming method correctly converts variations of words to their root stems
    @Test
    public void testStemmingTokenizer() {
        List<String> result = WordStemmer.splitWithStemming("stop stopper stopped stopping");

        long stopCount = result.stream()
                .filter(s -> s.equalsIgnoreCase("stop"))
                .count();

        // Porter stemmer reduces "stop", "stopped", and "stopping" to "stop" (3 occurrences)
        Assertions.assertEquals(3, stopCount, "Expected 'stop', 'stopped', and 'stopping' to stem to 'stop'");
    }

    @Test
    public void testStemmingWithUnicode() {
        String input = "😀 testing résumé naïve café"; // Includes emoji + accented characters
        List<String> result = WordStemmer.splitWithStemming(input);

        // Should process without throwing exceptions and properly stem 'testing' -> 'test'
        Assertions.assertFalse(result.isEmpty(), "Stemmed result list should not be empty");
        Assertions.assertTrue(result.contains("test"), "Result should contain the stem 'test' derived from 'testing'");
    }

    @Test
    public void testEmptyAndNullInput() {
        List<String> nullResult = WordStemmer.splitWithStemming(null);
        Assertions.assertNotNull(nullResult);
        Assertions.assertTrue(nullResult.isEmpty(), "Null input should return an empty list");

        List<String> emptyResult = WordStemmer.splitWithStemming("   ");
        Assertions.assertNotNull(emptyResult);
        Assertions.assertTrue(emptyResult.isEmpty(), "Whitespace input should return an empty list");
    }
}

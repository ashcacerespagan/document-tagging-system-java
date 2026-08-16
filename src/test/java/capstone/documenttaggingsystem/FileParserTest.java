package capstone.documenttaggingsystem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * Unit test suite for verifying FileParser text normalization logic.
 */
public class FileParserTest {

    private final FileParser fileParser = new FileParser();

    private String getTestFilePath(String fileName) {
        return System.getProperty("user.dir") 
                + File.separator + "testDocuments" 
                + File.separator + "parser" 
                + File.separator + fileName;
    }

    // Checks that the parser doesn't modify a clean file
    @Test
    public void testClean() {
        String result = fileParser.convertFileToAlphanumericString(getTestFilePath("testClean.txt"));
        Assertions.assertEquals("just a clean test document for checking tf idf keyword extraction", result);
    }

    // Checks how parser handles an empty file
    @Test
    public void testEmpty() {
        String result = fileParser.convertFileToAlphanumericString(getTestFilePath("testEmpty.txt"));
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isEmpty(), "Result for empty file should be an empty string");
    }

    // Checks how parser handles long single-word strings
    @Test
    public void testLongSingleWord() {
        String result = fileParser.convertFileToAlphanumericString(getTestFilePath("testLongSingleWord.txt"));
        Assertions.assertTrue(result.contains("supercalifragilisticexpialidociousandthensomeextralongmadeupwordthatneverendsjusttochecklimits"));
    }

    // Checks non-alphabetic characters are removed
    @Test
    public void testNonAlpha() {
        String result = fileParser.convertFileToAlphanumericString(getTestFilePath("testNonAlpha.txt"));
        Assertions.assertEquals("this document has weird characters that should be removed", result);
    }

    // Checks that numbers are preserved
    @Test
    public void testNumbersOnly() {
        String result = fileParser.convertFileToAlphanumericString(getTestFilePath("testNumbersOnly.txt"));
        Assertions.assertEquals("123 4567 890 42 1999 2024 314", result);
    }

    // Checks that only symbols are removed
    @Test
    public void testOnlySymbols() {
        String result = fileParser.convertFileToAlphanumericString(getTestFilePath("testOnlySymbols.txt"));
        Assertions.assertTrue(result.isEmpty(), "All symbols should be removed, leaving an empty string");
    }

    // Checks that tabs and newlines are converted to spaces and collapsed
    @Test
    public void testTabsNewlines() {
        String result = fileParser.convertFileToAlphanumericString(getTestFilePath("testTabsNewlines.txt"));
        Assertions.assertEquals("this is a test with newlines and tabs to clean up", result);
    }

    // Checks that Unicode characters are filtered out
    @Test
    public void testUnicode() {
        String result = fileParser.convertFileToAlphanumericString(getTestFilePath("testUnicode.txt"));
        Assertions.assertEquals("this uc0u55357 u56846 test u55358 u56598 has emojis 97 and accented characters like naefve cafe9 pif1ata", result);
    }

    // Checks that uppercase text is lowercased
    @Test
    public void testUppercase() {
        String result = fileParser.convertFileToAlphanumericString(getTestFilePath("testUppercase.txt"));
        Assertions.assertEquals("this file is written in all capital letters for testing purposes only", result);
    }
}

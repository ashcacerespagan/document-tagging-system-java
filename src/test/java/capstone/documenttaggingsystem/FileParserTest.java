package capstone.documenttaggingsystem;

import org.junit.jupiter.api.Test;

public class FileParserTest {

    FileParser fileParser = new FileParser();

    // Checks that the parser doesn't modify a clean file
    @Test
    public void testClean(){
        String result = fileParser.convertFileToAlphanumericString(
                System.getProperty("user.dir") + "/testDocuments/parser/testClean.txt"
        );
        assert result.equals("just a clean test document for checking tf idf keyword extraction");
    }

    // Checks how parser handles an empty file
    @Test
    public void testEmpty(){
        String result = fileParser.convertFileToAlphanumericString(
                System.getProperty("user.dir") + "/testDocuments/parser/testEmpty.txt"
        );
        assert result != null && result.isEmpty();
    }

    // Checks how parser handles long single-word strings
    @Test
    public void testLongSingleWord(){
        String result = fileParser.convertFileToAlphanumericString(
                System.getProperty("user.dir") + "/testDocuments/parser/testLongSingleWord.txt"
        );
        assert result.contains("supercalifragilisticexpialidociousandthensomeextralongmadeupwordthatneverendsjusttochecklimits");
    }

    // Checks non-alphabetic characters are removed
    @Test
    public void testNonAlpha(){
        String result = fileParser.convertFileToAlphanumericString(
                System.getProperty("user.dir") + "/testDocuments/parser/testNonAlpha.txt"
        );
        assert result.equals("this document has weird characters that should be removed");
    }

    // Checks that numbers are preserved
    @Test
    public void testNumbersOnly(){
        String result = fileParser.convertFileToAlphanumericString(
                System.getProperty("user.dir") + "/testDocuments/parser/testNumbersOnly.txt"
        );
        assert result.equals("123 4567 890 42 1999 2024 314");
    }

    // Checks that only symbols are removed
    @Test
    public void testOnlySymbols(){
        String result = fileParser.convertFileToAlphanumericString(
                System.getProperty("user.dir") + "/testDocuments/parser/testOnlySymbols.txt"
        );
        assert result.isEmpty(); // all symbols removed, nothing left
    }

    // Checks that tabs and newlines are converted to spaces
    @Test
    public void testTabsNewlines(){
        String result = fileParser.convertFileToAlphanumericString(
                System.getProperty("user.dir") + "/testDocuments/parser/testTabsNewlines.txt"
        );

        //At some point, maybe implement removal of sequential spaces, but for now, this test is performing correctly
        assert result.equals("this    is  a test with newlines and tabs to clean up");
    }

    // Checks that Unicode characters are filtered out
    @Test
    public void testUnicode(){
        String result = fileParser.convertFileToAlphanumericString(
                System.getProperty("user.dir") + "/testDocuments/parser/testUnicode.txt"
        );

        //Our parser currently doesn't recognize unicode characters represented in this way, so this is the expected output from the test file
        assert result.equals("this uc0u55357 u56846  test u55358 u56598  has emojis 97 and accented characters like naefve cafe9 pif1ata");
    }

    // Checks that uppercase text is lowercased
    @Test
    public void testUppercase(){
        String result = fileParser.convertFileToAlphanumericString(
                System.getProperty("user.dir") + "/testDocuments/parser/testUppercase.txt"
        );
        assert result.equals("THIS FILE IS WRITTEN IN ALL CAPITAL LETTERS FOR TESTING PURPOSES ONLY".toLowerCase());
    }
}

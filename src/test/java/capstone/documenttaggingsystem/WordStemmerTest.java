package capstone.documenttaggingsystem;

import org.junit.jupiter.api.Test;

import java.util.List;

public class WordStemmerTest {

    //Verifies that the stemming method correctly converts words to their stems
    @Test
    public void testStemmingTokenizer() {
        List<String> result = WordStemmer.splitWithStemming("stop stopper stopped stopping");

        int stopCount = 0;

        for(String string : result){
            if(string.equalsIgnoreCase("stop")) stopCount++;
        }

        assert stopCount == 3;
    }

    @Test
public void testStemmingWithUnicode() {
    String input = "😀 testing résumé naïve café"; // includes emoji + accented characters
    List<String> result = WordStemmer.splitWithStemming(input);

    // Should not throw exceptions or crash, even if some characters are stripped
        assert !result.isEmpty(); // should at least include "testing"

    // Optional: ensure stemming fallback doesn't collapse everything
    assert result.contains("test");
    }

}

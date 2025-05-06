package capstone.documenttaggingsystem;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Class which provides a static method for splitting an input document
 * into its component word stems.
 */
public class WordStemmer {

    /**
     * Splits the inputDocument using the PorterStemFilter to stem words
     * down to their root, making words like "stop", "stopping", and "stopped"
     * the same word: "stop"
     *
     * @param inputDocument The document to be split
     * @return The split, stemmed document
     */
    public static List<String> splitWithStemming(String inputDocument) {
        List<String> stringList = new ArrayList<>();

        try {
            // Use factory method to create the tokenizer (Lucene 9+ syntax)
            WhitespaceTokenizerFactory tokenizerFactory = new WhitespaceTokenizerFactory(new HashMap<>());
            Tokenizer tokenizer = tokenizerFactory.create();
            tokenizer.setReader(new StringReader(inputDocument));

            // Apply the stemming filter
            TokenStream tokenStream = new PorterStemFilter(tokenizer);
            tokenStream.reset();

            CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
            while (tokenStream.incrementToken()) {
                stringList.add(attr.toString());
            }

            tokenStream.end();
            tokenStream.close();
        } catch (IOException e) {
            System.err.println("Error during stemming: " + e.getMessage());
        }

        return stringList;
    }
}

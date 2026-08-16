package capstone.documenttaggingsystem;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class providing static methods for tokenizing input text
 * and applying the Porter Stemming algorithm via Apache Lucene.
 */
public class WordStemmer {

    /**
     * Splits an input document into whitespace tokens and applies the PorterStemFilter
     * to stem words down to their root form (e.g., "testing", "tested" -> "test").
     *
     * @param inputDocument The document string to be tokenized and stemmed
     * @return List of stemmed token strings
     */
    public static List<String> splitWithStemming(String inputDocument) {
        if (inputDocument == null || inputDocument.isBlank()) {
            return Collections.emptyList();
        }

        List<String> stringList = new ArrayList<>();

        try {
            WhitespaceTokenizerFactory tokenizerFactory = new WhitespaceTokenizerFactory(new HashMap<>());
            Tokenizer tokenizer = tokenizerFactory.create();
            tokenizer.setReader(new StringReader(inputDocument));

            try (TokenStream tokenStream = new PorterStemFilter(tokenizer)) {
                tokenStream.reset();
                CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);

                while (tokenStream.incrementToken()) {
                    String token = attr.toString().trim();
                    if (!token.isEmpty()) {
                        stringList.add(token);
                    }
                }

                tokenStream.end();
            }
        } catch (IOException e) {
            System.err.println("Error during stemming tokenization: " + e.getMessage());
        }

        return stringList;
    }
}

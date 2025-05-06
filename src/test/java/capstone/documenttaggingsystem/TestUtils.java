package capstone.documenttaggingsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils {

    /**
     * Generates mocked test documents for tf purposes.
     *
     * @return A list of test documents.
     */
    public static List<String> generateTestStrings(){

        List<String> result = new ArrayList<>();
        result.add("this is for testing purposes");
        result.add("this this is for testing");
        result.add("just a testing string");

        return result;
    }

    /**
     * Generates mocked idf map for use with the test
     * documents in this class.
     *
     * @return A testing idf map.
     */
    public static Map<String, Double> generateIdfMap(){
        Map<String, Double> result = new HashMap<>();

        result.put("this", Math.log(3.0/2.0));
        result.put("is", Math.log(3.0/2.0));
        result.put("for", Math.log(3.0/2.0));
        result.put("testing", Math.log(1.0));
        result.put("purposes", Math.log(3.0));
        result.put("just", Math.log(3.0));
        result.put("a", Math.log(3.0));
        result.put("string", Math.log(3.0));

        //For manually verifying the values this outputs
//        result.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

        return result;
    }
}

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
public class IdfTrainerTest {

    @Mock
    FileParser fileParser;

    @InjectMocks
    IdfTrainer idfTrainer;

    /**
     * Tests that, if the FileParser returns the three test Strings when calls
     * are made to it, that it will train an Idf Map with the expected values.
     */
    @Test
    public void trainIdfMapHappyPath(){
        List<String> testStrings = TestUtils.generateTestStrings();

        Mockito.when(fileParser.convertFileToAlphanumericString(Mockito.anyString()))
                .thenReturn(testStrings.get(0))
                .thenReturn(testStrings.get(1))
                .thenReturn(testStrings.get(2));

        Map<String, Double> expected = TestUtils.generateIdfMap();

        idfTrainer.trainIdfMap(System.getProperty("user.dir") + "\\trainingData\\testSet1\\", false);

        Map<String, Double> actual = idfTrainer.getIdfMap();

        //For manually verifying the values this outputs
//        actual.forEach((key, value) -> System.out.println("Key: " + key + ", Value: " + value));

        Assertions.assertEquals(expected, actual, "Generated IDF map does not match expected values.");

    }
    @Test
    public void trainIdfMapWithEmptyDirShouldFail() {
        boolean result = idfTrainer.trainIdfMap("path/that/does/not/exist", false);
        Assertions.assertFalse(result, "Training should fail for non-existent or empty directory.");
    }
}

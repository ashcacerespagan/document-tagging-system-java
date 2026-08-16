package capstone.documenttaggingsystem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Unit test suite for verifying IDF training algorithms and mocked file parsing workflows.
 */
@ExtendWith(MockitoExtension.class)
public class IdfTrainerTest {

    @Mock
    FileParser fileParser;

    @InjectMocks
    IdfTrainer idfTrainer;

    /**
     * Tests that when FileParser returns the predefined test strings,
     * IdfTrainer generates an IDF map matching expected theoretical values.
     */
    @Test
    public void trainIdfMapHappyPath() {
        List<String> testStrings = TestUtils.generateTestStrings();

        Mockito.when(fileParser.convertFileToAlphanumericString(Mockito.anyString()))
                .thenReturn(testStrings.get(0))
                .thenReturn(testStrings.get(1))
                .thenReturn(testStrings.get(2));

        Map<String, Double> expected = TestUtils.generateIdfMap();

        String trainingDirPath = System.getProperty("user.dir") 
                + File.separator + "trainingData" 
                + File.separator + "testSet1" 
                + File.separator;

        boolean success = idfTrainer.trainIdfMap(trainingDirPath, false);
        Assertions.assertTrue(success, "Training should succeed for valid training directory");

        Map<String, Double> actual = idfTrainer.getIdfMap();

        Assertions.assertEquals(expected, actual, "Generated IDF map does not match expected values.");
    }

    @Test
    public void trainIdfMapWithEmptyDirShouldFail() {
        String invalidPath = System.getProperty("user.dir") 
                + File.separator + "path" 
                + File.separator + "that" 
                + File.separator + "does" 
                + File.separator + "not" 
                + File.separator + "exist";

        boolean result = idfTrainer.trainIdfMap(invalidPath, false);
        Assertions.assertFalse(result, "Training should fail for non-existent or empty directory.");
    }
}

package capstone.documenttaggingsystem;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

public class DevUtilityTest {

    /**
     * Generates a test IDF Map file using predefined strings.
     * Only use this manually to regenerate testIdfMap.txt in the idfMaps directory.
     */
    @Test
    public void generateTestIdfMapFile() {
        FileParser mockFileParser = Mockito.mock(FileParser.class);
        IdfTrainer trainer = new IdfTrainer(mockFileParser);

        List<String> testStrings = TestUtils.generateTestStrings();

        Mockito.when(mockFileParser.convertFileToAlphanumericString(Mockito.anyString()))
                .thenReturn(testStrings.get(0))
                .thenReturn(testStrings.get(1))
                .thenReturn(testStrings.get(2));

        trainer.createIdfMap(
                System.getProperty("user.dir") + "\\trainingData\\testSet1\\",
                System.getProperty("user.dir") + "\\idfMaps\\testIdfMap.txt",
                false
        );
    }
}
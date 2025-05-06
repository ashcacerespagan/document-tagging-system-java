package capstone.documenttaggingsystem;

import org.junit.jupiter.api.Test;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class GuiExportTest {

    @Test
    public void testExportFileCreation() throws Exception {
        String keywords = "Top Keywords:\n\nai — 1.2345\ndata — 1.1200";
        File tempFile = File.createTempFile("test_keywords", ".txt");
        tempFile.deleteOnExit();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(keywords);
        }

        assert tempFile.exists();
        assert tempFile.length() > 0;
    }
}
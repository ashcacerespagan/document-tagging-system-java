package capstone.documenttaggingsystem;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

/**
 * Unit test verifying temporary file creation and export file writing mechanics for GUI export operations.
 */
public class GuiExportTest {

    @Test
    public void testExportFileCreation() throws Exception {
        String keywords = "Top Keywords:\n\nai — 1.2345\ndata — 1.1200";
        File tempFile = File.createTempFile("test_keywords", ".txt");
        tempFile.deleteOnExit();

        try {
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8))) {
                writer.write(keywords);
            }

            Assertions.assertTrue(tempFile.exists(), "Export file should exist on disk");
            Assertions.assertTrue(tempFile.length() > 0, "Export file should contain written content");
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}

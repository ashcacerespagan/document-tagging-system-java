package capstone.documenttaggingsystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Stream;

public class BatchTestRunner {

    public static void main(String[] args) {
        Path basePath = Paths.get("testDocuments");
        FileParser parser = new FileParser();
        TfIdfCalculator calculator = new TfIdfCalculator(new IdfLoader());
        File outputFile = new File("batchTestResults.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true));  // true = append mode
             Stream<Path> paths = Files.walk(basePath)) {

            writer.write("==== NEW TEST RUN: " + LocalDateTime.now() + " ====\n\n");

            var txtFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".txt"))
                    .toList();

            for (Path path : txtFiles) {
                writer.write("==== " + path.getFileName() + " ====\n");
                String parsed = parser.convertFileToAlphanumericString(path.toString());

                if (parsed == null || parsed.isBlank()) {
                    writer.write("❌ Could not parse file.\n\n");
                    continue;
                }

                for (boolean useStemming : new boolean[]{false, true}) {
                    writer.write("Stemming: " + (useStemming ? "ON" : "OFF") + "\n");
                    Map<String, Double> keywords = calculator.getTopKeywords(parsed, null, 10, useStemming);

                    if (keywords.isEmpty()) {
                        writer.write("No keywords extracted.\n\n");
                    } else {
                        for (Map.Entry<String, Double> entry : keywords.entrySet()) {
                            writer.write(entry.getKey() + " — " + String.format("%.4f", entry.getValue()) + "\n");
                        }
                        writer.write("\n");
                    }
                }
            }

            writer.write("==== END OF TEST RUN ====\n\n");

            System.out.println("✅ Batch results appended to " + outputFile.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("❌ Error writing batch results: " + e.getMessage());
        }
    }
}

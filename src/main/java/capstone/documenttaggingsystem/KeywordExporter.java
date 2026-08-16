package capstone.documenttaggingsystem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Exporter utility for serializing top keywords into TXT, CSV, and JSON formats.
 */
public class KeywordExporter {

    public enum Format {
        TXT, CSV, JSON
    }

    public static void exportToFile(Map<String, Double> keywords, File destinationFile, Format format) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(destinationFile), StandardCharsets.UTF_8))) {
            
            switch (format) {
                case CSV -> writeCsv(keywords, writer);
                case JSON -> writeJson(keywords, writer);
                default -> writeTxt(keywords, writer);
            }
        }
    }

    private static void writeTxt(Map<String, Double> keywords, BufferedWriter writer) throws IOException {
        writer.write("=======================================\n");
        writer.write("Top Extracted Keywords\n");
        writer.write("=======================================\n\n");
        for (Map.Entry<String, Double> entry : keywords.entrySet()) {
            writer.write(String.format("%-18s — %.4f\n", entry.getKey(), entry.getValue()));
        }
    }

    private static void writeCsv(Map<String, Double> keywords, BufferedWriter writer) throws IOException {
        writer.write("Keyword,Score\n");
        for (Map.Entry<String, Double> entry : keywords.entrySet()) {
            writer.write(String.format("\"%s\",%.6f\n", entry.getKey(), entry.getValue()));
        }
    }

    private static void writeJson(Map<String, Double> keywords, BufferedWriter writer) throws IOException {
        writer.write("{\n");
        writer.write("  \"extracted_keywords\": [\n");
        int index = 0;
        int size = keywords.size();
        for (Map.Entry<String, Double> entry : keywords.entrySet()) {
            writer.write(String.format("    {\"keyword\": \"%s\", \"score\": %.6f}%s\n",
                    entry.getKey(), entry.getValue(), (index == size - 1) ? "" : ","));
            index++;
        }
        writer.write("  ]\n");
        writer.write("}\n");
    }
}
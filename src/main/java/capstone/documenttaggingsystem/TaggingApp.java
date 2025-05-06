package capstone.documenttaggingsystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

public class TaggingApp extends Application {

    private final FileParser fileParser = new FileParser();
    private final TfIdfCalculator tfIdfCalculator = new TfIdfCalculator(new IdfLoader());
    private final Label keywordCountLabel = new Label("");

    private final String lightTheme = "-fx-background-color: #f9f9f9; -fx-text-fill: black;";
    private final String darkTheme = "-fx-background-color: #2b2b2b; -fx-text-fill: white;";
    private boolean isDarkMode = false;
    private final VBox layout = new VBox(); // Ensure it's initialized
    @Getter
    private final StringBuilder result = new StringBuilder();
    @Getter
    private final StringBuilder exportPath = new StringBuilder();
    @Getter
    private final StringBuilder exportFile = new StringBuilder();

    // Required default constructor for JavaFX
    public TaggingApp() {
        // layout already initialized inline
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("🧠 Document Tagging System");

        String commonFont = "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 13px;";

        Label keywordLabel = new Label("How many keywords to show (1–20)?");
        keywordLabel.setStyle(commonFont);

        TextField keywordInput = new TextField("10");
        keywordInput.setTooltip(new Tooltip("Enter a number between 1 and 20"));
        keywordInput.setStyle(commonFont);

        CheckBox stemmingCheckbox = new CheckBox("Use Stemming");
        stemmingCheckbox.setStyle(commonFont);

        CheckBox darkModeCheckbox = new CheckBox("🌙 Dark Mode");
        darkModeCheckbox.setStyle(commonFont);
        Label selectedFileLabel = new Label("No file selected.");
        selectedFileLabel.setStyle(commonFont);

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: darkgreen; -fx-font-size: 12px;");

        Button selectFileBtn = new Button("📁 Choose .txt File");
        Button clearBtn = new Button("🧹 Clear");
        styleButton(selectFileBtn);
        styleButton(clearBtn);

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setWrapText(true);
        outputArea.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");
        ScrollPane scrollPane = new ScrollPane(outputArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setPrefHeight(200);

        ComboBox<String> exportFormatCombo = new ComboBox<>();
        exportFormatCombo.getItems().addAll("TXT", "CSV");
        exportFormatCombo.setValue("TXT");

        Button exportBtn = new Button("⬇️ Export");
        styleButton(exportBtn);
        exportBtn.setDisable(true);

        HBox exportControls = new HBox(10, exportFormatCombo, exportBtn);
        exportControls.setAlignment(Pos.CENTER_LEFT);

        final String[] lastResult = {null};
        final File[] lastSelectedFile = {null};
        selectFileBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Text File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(stage);

            if (selectedFile != null) {
                lastSelectedFile[0] = selectedFile;
                selectedFileLabel.setText("Selected file: " + selectedFile.getName());

                String inputText = keywordInput.getText().trim();
                int numKeywords;

                try {
                    numKeywords = Integer.parseInt(inputText);
                    if (numKeywords < 1 || numKeywords > 20) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    outputArea.setText("❌ Please enter a valid number between 1 and 20.");
                    exportBtn.setDisable(true);
                    statusLabel.setText("⚠️ Invalid keyword count.");
                    keywordCountLabel.setText("");
                    return;
                }

                String parsed = fileParser.convertFileToAlphanumericString(selectedFile.getAbsolutePath());

                if (parsed == null || parsed.isBlank()) {
                    outputArea.setText("❌ Could not read or parse the selected file.");
                    exportBtn.setDisable(true);
                    statusLabel.setText("⚠️ File could not be parsed.");
                    keywordCountLabel.setText("");
                    return;
                }

                boolean useStemming = stemmingCheckbox.isSelected();
                Map<String, Double> keywords = tfIdfCalculator.getTopKeywords(parsed, null, numKeywords, useStemming);

                if (keywords.isEmpty()) {
                    outputArea.setText("No keywords could be extracted.");
                    exportBtn.setDisable(true);
                    statusLabel.setText("⚠️ No keywords found.");
                    keywordCountLabel.setText("");
                } else {
                    StringBuilder result = new StringBuilder("Top Keywords:\n\n");
                    keywords.forEach((k, v) -> result.append(k).append(" — ").append(String.format("%.4f", v)).append("\n"));
                    lastResult[0] = result.toString();
                    outputArea.setText(lastResult[0]);
                    exportBtn.setDisable(false);
                    keywordCountLabel.setText("🔢 " + keywords.size() + " keywords extracted.");
                    statusLabel.setText("✅ Keywords extracted.");
                }

            } else {
                outputArea.setText("⚠️ No file selected.");
                selectedFileLabel.setText("No file selected.");
                exportBtn.setDisable(true);
                statusLabel.setText("⚠️ No file selected.");
                keywordCountLabel.setText("");
            }
        });

        exportBtn.setOnAction(e -> {
            if (lastResult[0] != null && lastSelectedFile[0] != null) {
                try {
                    String originalName = lastSelectedFile[0].getName().replace(".txt", "");
                    String extension = exportFormatCombo.getValue().equals("CSV") ? "_keywords.csv" : "_keywords.txt";
                    String exportPath = lastSelectedFile[0].getParent() + File.separator + originalName + extension;

                    File exportFile = new File(exportPath);
                    writeExportFile(exportFile, exportFormatCombo.getValue(), lastResult[0]);

                    outputArea.appendText("\n\n✅ Exported to: " + exportPath);
                    statusLabel.setText("✅ Export complete.");

                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Keyword list exported successfully!");
                    alert.setTitle("Export Complete");
                    alert.setHeaderText(null);
                    alert.showAndWait();
                } catch (Exception ex) {
                    outputArea.appendText("\n\n❌ Failed to export file.");
                    statusLabel.setText("❌ Export failed.");
                }
            }
        });

        clearBtn.setOnAction(e -> {
            keywordInput.setText("10");
            stemmingCheckbox.setSelected(false);
            selectedFileLabel.setText("No file selected.");
            outputArea.clear();
            exportBtn.setDisable(true);
            statusLabel.setText("🧼 Cleared.");
            keywordCountLabel.setText("");
        });

        darkModeCheckbox.setOnAction(e -> {
    isDarkMode = darkModeCheckbox.isSelected();
    String baseStyle = "-fx-font-family: 'Segoe UI', sans-serif; -fx-font-size: 13px;";
    String bgStyle = isDarkMode ? darkTheme : lightTheme;

    layout.setStyle(bgStyle + baseStyle);
    keywordLabel.setStyle(baseStyle + (isDarkMode ? "-fx-text-fill: white;" : "-fx-text-fill: black;"));
    keywordInput.setStyle(bgStyle + baseStyle);
    stemmingCheckbox.setStyle(baseStyle + (isDarkMode ? "-fx-text-fill: white;" : "-fx-text-fill: black;"));
    darkModeCheckbox.setStyle(baseStyle + (isDarkMode ? "-fx-text-fill: white;" : "-fx-text-fill: black;"));
    selectedFileLabel.setStyle(baseStyle + (isDarkMode ? "-fx-text-fill: white;" : "-fx-text-fill: black;"));
    statusLabel.setStyle((isDarkMode ? "-fx-text-fill: lightgreen;" : "") + "-fx-font-size: 12px;");
    keywordCountLabel.setStyle(baseStyle + (isDarkMode ? "-fx-text-fill: white;" : "-fx-text-fill: black;"));
outputArea.setStyle(
    "-fx-font-family: monospace; -fx-font-size: 12px;" +
    (isDarkMode
        ? "-fx-highlight-fill: #444; -fx-highlight-text-fill: white;"
        : "-fx-control-inner-background: white; -fx-text-fill: black;")
);
});


        layout.getChildren().setAll(
                keywordLabel,
                keywordInput,
                stemmingCheckbox,
                darkModeCheckbox,
                selectFileBtn,
                selectedFileLabel,
                scrollPane,
                keywordCountLabel,
                exportControls,
                clearBtn,
                statusLabel
        );
        layout.setPadding(new Insets(15));
        layout.setStyle(lightTheme + commonFont);

        Scene scene = new Scene(layout, 560, 600);
        stage.setScene(scene);

        stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - 560) / 2);
        stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - 600) / 2);

        stage.show();
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-font-size: 13px; -fx-cursor: hand;");
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> button.setStyle("-fx-background-color: #d9d9d9; -fx-font-size: 13px; -fx-cursor: hand;"));
        button.addEventHandler(MouseEvent.MOUSE_EXITED, e -> button.setStyle("-fx-font-size: 13px; -fx-cursor: hand;"));
    }

    private void writeExportFile(File exportFile, String exportFormat, String resultText) throws Exception {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(exportFile))) {
            if (exportFormat.equals("CSV")) {
                writer.write("Keyword,Score\n");
                String[] lines = resultText.split("\n");
                for (String line : lines) {
                    if (line.contains(" — ")) {
                        String[] parts = line.split(" — ");
                        writer.write(parts[0] + "," + parts[1] + "\n");
                    }
                }
            } else {
                writer.write(resultText);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

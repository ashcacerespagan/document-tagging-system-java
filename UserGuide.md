# Document Tagging System – User Guide

## 1. Requirements
* Java 21+
* Gradle 8+ or wrapper (`./gradlew`)
* `xvfb` (for headless Linux/Codespaces)

## 2. Launch Options
* **GUI via Gradle:** `xvfb-run ./gradlew run`
* **GUI via Fat JAR:** `./gradlew shadowJar && xvfb-run java -jar build/libs/document-tagging-system-1.0-all.jar`
* **CLI via Gradle:** `xvfb-run ./gradlew run -PmainClass=capstone.documenttaggingsystem.TaggingCli`

## 3. Usage
* **GUI:** Pick a `.txt` file -> Set count (1-20) -> Toggle Stemming/Dark Mode -> View scores -> Export to TXT, CSV, or JSON.
* **CLI:** Option 1 to extract keywords from file; Option 2 to train IDF map from a directory of `.txt` files.

## 4. Output Formats
* **TXT:** `keyword — 0.1234`
* **CSV:** `"keyword",0.123456`
* **JSON:** `{"extracted_keywords": [{"keyword": "data", "score": 0.123456}]}`

## 5. Troubleshooting
* **JavaFX Runtime Missing:** Launch via `MainLauncher` or Shadow JAR, not directly via `TaggingApp`.
* **Unsupported JavaFX Config:** Safe warning when running JavaFX from a single-module Fat JAR.
* **Invalid Number:** Keyword count must be an integer between 1 and 20.
# Document Tagging System

A JavaFX and CLI-based document parsing and TF-IDF keyword extraction engine built with Java 21, Apache Lucene, and Gradle. The project extracts, ranks, and exports key terms from text documents using term frequency and inverse document frequency scoring.

## What It Does

- Extracts and ranks keywords from `.txt` documents using TF-IDF logic.
- Filters out common English stop words (*"the"*, *"and"*, *"is"*) for improved keyword relevance.
- Supports flexible keyword ranking counts (1 to 20 terms).
- Applies Porter Stemming via Apache Lucene's analysis framework.
- Offers dual execution interfaces: JavaFX Desktop GUI and Terminal CLI.
- Exports keyword results into TXT, CSV, or JSON formats (`KeywordExporter`).
- Packages as a single standalone executable Fat JAR (`shadowJar`).

## Tech Stack

- **Language:** Java 21
- **UI Framework:** JavaFX 21.0.7
- **Text Analysis:** Apache Lucene Core 10.2.0 (Porter Stemmer)
- **Testing & Mocking:** JUnit 5, Mockito 5.11
- **Build System:** Gradle 9.4 (via Gradle Wrapper)
- **Packaging:** Shadow Plugin (`com.gradleup.shadow`)

## Project Architecture
src/
├── main/
│   └── java/
│       └── capstone/
│           └── documenttaggingsystem/
│               ├── FileParser.java
│               ├── FileUtils.java
│               ├── IdfLoader.java
│               ├── IdfTrainer.java
│               ├── KeywordExporter.java
│               ├── MainLauncher.java
│               ├── TaggingApp.java
│               ├── TaggingCli.java
│               ├── TfIdfCalculator.java
│               └── WordStemmer.java
└── test/
└── java/
└── capstone/
└── documenttaggingsystem/
├── BackendIntegrationTest.java
├── FileParserTest.java
├── GuiExportTest.java
├── IdfLoaderTest.java
├── IdfTrainerTest.java
├── TestUtils.java
├── TfIdfCalculatorTest.java
└── WordStemmerTest.java

## Quick Start & Execution

### Prerequisites
- JDK 21 or higher
- Gradle Wrapper (included)

### Build & Run Unit Tests
Run the 31-test suite across Linux, macOS, or Windows environments:

# Run test suite
xvfb-run ./gradlew test

# Full clean build
xvfb-run ./gradlew clean build

Launching the Application
1. JavaFX GUI Interface
xvfb-run ./gradlew run
2. Terminal CLI Interface
xvfb-run ./gradlew run -PmainClass=capstone.documenttaggingsystem.TaggingCli
3. Standalone Executable Fat JAR
Build and run the standalone, single-file JAR package without needing Gradle installed:

# Generate the Fat JAR
./gradlew shadowJar

# Execute directly with Java 21
xvfb-run java -jar build/libs/document-tagging-system-1.0-all.jar
Main Features & GUI Workflow
File Selection: Choose any .txt document.

Configuration: Set desired keyword count (1–20) and toggle Porter Stemming.

Theme Toggle: Switch between Light and Dark modes dynamically.

Keyword Scoring: View real-time ranked keywords based on TF-IDF weights.

Multi-Format Export: Export extracted keywords to .txt, .csv, or .json.
# Document Tagging System

Java-based document tagging system that extracts and ranks keywords from `.txt` files using TF-IDF logic. The project includes both a JavaFX GUI and a command-line interface.

## What It Does

- Extracts keywords from `.txt` documents
- Ranks keywords by importance using TF-IDF
- Supports keyword counts from 1 to 20
- Allows stemming with Apache Lucene’s PorterStemmer
- Supports both GUI and CLI usage
- Exports keyword results to TXT or CSV
- Includes batch testing for multiple `.txt` files

## Tech Used

- Java 21
- JavaFX 24.0.1
- Gradle
- Apache Lucene PorterStemmer
- TF-IDF keyword extraction
- CLI and GUI workflows

## Main Features

- JavaFX graphical interface
- Command-line interface mode
- File selection for `.txt` documents
- Stemming toggle
- Dark mode toggle
- TXT and CSV export
- IDF map training through CLI
- Batch testing with timestamped results
- Error handling for empty files, invalid keyword counts, export failures, and unreadable content

## GUI Workflow

1. Choose a `.txt` file
2. Enter a keyword count from 1 to 20
3. Optionally enable stemming
4. Optionally enable dark mode
5. View ranked keywords
6. Export results as TXT or CSV
7. Clear the form when finished

## CLI Options

The CLI supports:

1. Extract Keywords  
2. Train IDF Map  
3. Exit  

Users can provide file paths, IDF map paths, keyword count, and stemming settings directly through the console.

## Batch Testing

`BatchTestRunner.java` processes all `.txt` files under the `testDocuments/` folder, including subfolders. It runs tests with stemming on and off, then saves results to:

```txt
batchTestResults.txt

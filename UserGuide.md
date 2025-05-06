🧭 Document Tagging System – User Guide
This guide walks you through how to use both the Graphical User Interface (GUI) and Command Line Interface (CLI) in the Document Tagging System. It also explains file output, common errors, and what each component does.

✅ 1. System Requirements

Requirement	Version
Java	21 or newer
Gradle	Installed (or use IntelliJ to run Gradle tasks)
JavaFX SDK	24.0.1 configured in IntelliJ or set in environment path
🚀 2. Launching the Application
You can launch the app in two modes:

• Graphical User Interface (GUI):
Run this from terminal or IntelliJ:

bash
Copy
Edit
./gradlew run
• Command Line Interface (CLI):
bash
Copy
Edit
./gradlew run --args='cli'
🖼️ 3. GUI Usage
➤ Basic Workflow:
Click "Choose File" → select a .txt file from your system.

Set keyword count (enter a number from 1–20).

(Optional) Check "Use Stemming" to simplify word forms (e.g., “stopped” → “stop”).

(Optional) Toggle Dark Mode for better visibility.

View extracted keywords in the output box.

Choose export format (TXT or CSV) using the dropdown.

Click "Export" to save results in the same folder as the input file.

Use "Clear" to reset all fields and outputs.

💡 GUI Tips:
You’ll see the selected file name and keyword count dynamically update.

Hovering over inputs gives helpful tooltips.

Dark mode dynamically adjusts all styles for accessibility.

Keywords are ranked by importance using TF-IDF logic.

🔧 4. CLI Usage
After launching the CLI, you'll see:

makefile
Copy
Edit
1: Extract Keywords
2: Train IDF Map
3: Exit
➤ Option 1: Extract Keywords
Enter path to .txt file

Enter path to IDF map

Set keyword count (1–20)

Choose whether to use stemming

➤ Option 2: Train IDF Map
Enter folder path containing .txt training files

Provide a destination file path for saving the map

Choose whether to apply stemming during training

All outputs are printed directly in the console.

📂 5. Output Locations

Mode	File Format & Location
GUI	Exported to same folder as selected file:
filename_keywords.txt or .csv
CLI	Output shown in console; IDF maps saved to user-provided path
Batch	Results saved to batchTestResults.txt in project root (includes timestamp per run)
❗ 6. Error Handling & Feedback

Error Message	What It Means / How to Fix
"Please enter a valid number"	Keyword count must be an integer from 1 to 20
"Could not parse file"	File is empty, corrupted, or only symbols
"No keywords extracted"	Content may not contain meaningful words
"Failed to export file"	Check file permissions or existing file conflicts
"File is empty"	The system read nothing from the input .txt
🧪 7. Batch Testing
Use BatchTestRunner.java to automatically:

Process all .txt files under testDocuments/ (including subfolders)

Run both stemming ON and OFF

Save output to batchTestResults.txt with date/time stamps

Useful for QA, regression, and validation across test sets

🖼️ 8. Screenshots
You’ll find GUI previews under the /screenshots folder:

gui-light.png

gui-dark.png

gui-stemming-light.png

gui-stemming-dark.png

These show both themes and keyword variations depending on the settings.

🛠️ 9. Additional Notes
The system only supports .txt files — no PDFs, DOCX, etc.

Stemming uses Apache Lucene’s PorterStemmer

JavaFX styles and layout respond dynamically to user actions

📅 Last Updated
April 21, 2025
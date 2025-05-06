package capstone.documenttaggingsystem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

    // This grabs every .txt file in a folder, even if they're in subfolders
    public static List<String> getAllTxtFilePaths(String directoryPath) {
        List<String> filePaths = new ArrayList<>();
        File dir = new File(directoryPath);

        // Just making sure it exists and is a folder
        if (dir.exists() && dir.isDirectory()) {
            recurseDirectory(dir, filePaths);
        }

        return filePaths;
    }

    // Walk through all folders and subfolders and collect .txt file paths
    private static void recurseDirectory(File dir, List<String> filePaths) {
        File[] children = dir.listFiles();
        if (children != null) {
            for (File file : children) {
                if (file.isDirectory()) {
                    recurseDirectory(file, filePaths);
                } else if (file.getName().toLowerCase().endsWith(".txt")) {
                    filePaths.add(file.getAbsolutePath());
                }
            }
        }
    }
}

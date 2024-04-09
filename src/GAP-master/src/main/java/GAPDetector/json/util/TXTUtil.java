package GAPDetector.json.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TXTUtil {
    public static HashMap<String, ArrayList<String>> readCoreFile(String filePath) {
        HashMap<String, ArrayList<String>> coreFileList = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            String lastCategory = "";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split("\\s+");
                    if (parts.length > 1) {
                        coreFileList.get(lastCategory).add(parts[1]);
                    } else {
                        lastCategory = parts[0];
                        coreFileList.computeIfAbsent(lastCategory, k -> new ArrayList<>());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return coreFileList;
    }
}

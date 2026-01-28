package com.zmpc.util;

import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    public static String readTextFromFile(String filePath) throws Exception {
        return Files.readString(Path.of(filePath));
    }

    public static void writeTextToFile(String text, String filePath) throws Exception {
        Files.writeString(Path.of(filePath), text);
    }
}

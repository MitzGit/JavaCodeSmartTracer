package com.zmpc.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {

    public static String readTextFromFile(String filePath) throws IOException {
        return Files.readString(Path.of(filePath));
    }

    public static void writeTextToFile(String text, String filePath) throws IOException {
        Files.writeString(Path.of(filePath), text);
    }
}

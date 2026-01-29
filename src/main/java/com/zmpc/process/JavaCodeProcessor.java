package com.zmpc.process;

import java.util.HashMap;
import java.util.Map;

import static com.zmpc.common.Print.println;

public class JavaCodeProcessor {

    private final static Map<String, String> vStringsMap = new HashMap<>();

    public static String processJavaCodePrepare(String text) {
        text = processStringsPrepare(text);
        return text;
    }

    public static String processJavaCodeComplete(String text) {
        text = processStringsComplete(text);
        return text;
    }

    public static String processStringsPrepare(String text) {
        text = text.replace("https://", "##HTTPS##");
        text = text.replace("http://", "##HTTP##");

        // process """str""" - block strings
        text = processBlockStrings(text);

        // process "str" - common strings
        text = processLineStrings(text);

        return text;
    }

    private static String processBlockStrings(String text) {
        var indexStart = 0;
        var indexEnd = 0;

        var count = 10;
        String mapElemKey;
        String strOrigin;

        do {
            indexStart = text.indexOf("\"\"\"", indexStart);
            if (indexStart >= 0) {
                indexEnd = text.indexOf("\"\"\"", indexStart + 3);
                if (indexEnd > indexStart) {
                    count++;

                    strOrigin = text.substring(indexStart, indexEnd + 3);

                    mapElemKey = "#STRB" + count + "#";
                    vStringsMap.put(mapElemKey, strOrigin);
                    println(">> " + mapElemKey + " = " + strOrigin);

                    text = text.substring(0, indexStart)
                            + mapElemKey
                            + text.substring(indexEnd + 3);

                    indexStart = indexEnd + 3 + (mapElemKey.length() - strOrigin.length());
                }
            }
        } while (indexStart >= 0 && indexEnd > 0 && count < 10000);

        return text;
    }

    private static String processLineStrings(String text) {
        var indexStart = 0;
        var indexEnd = 0;
        var resultText = "";

        var count = 100;
        String mapElemKey;
        String strOrigin;
        String line;

        String[] lines = text.split("\n");
        int commentIndex;
        // process "str" - common strings
        for (var i = 0; i < lines.length; i++) {
            line = lines[i];

            do {

                indexStart = line.indexOf('"');
                if (indexStart >= 0) {
                    indexEnd = line.indexOf('"', indexStart + 1);
                    if (indexEnd > indexStart) {
                        commentIndex = line.indexOf("//");
                        if (commentIndex >= 0 && commentIndex < indexStart) {
                            // don't process strings after comment: //
                            break;
                        }

                        count++;

                        strOrigin = line.substring(indexStart, indexEnd + 1);

                        mapElemKey = "#STR" + count + '#';
                        vStringsMap.put(mapElemKey, strOrigin);
                        println(">> " + mapElemKey + " = " + strOrigin);

                        line = line.substring(0, indexStart)
                                + mapElemKey
                                + line.substring(indexEnd + 1);
                    }
                }
            } while (indexStart >= 0 && indexEnd > 0 && count < 100000);

            if (i > 0) {
                resultText += '\n';
            }
            resultText += line;
        }

        return resultText;
    }

    public static String processStringsComplete(String text) {
        for (Map.Entry<String, String> mapElem : vStringsMap.entrySet()) {
            text = text.replace(mapElem.getKey(), mapElem.getValue());
        }

        text = text.replace("##HTTPS##", "https://");
        text = text.replace("##HTTP##", "http://");

        return text;
    }
}

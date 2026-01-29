package com.zmpc.process;

import java.util.HashMap;
import java.util.Map;

import static com.zmpc.common.Print.println;

public class JavaCodeProcessor {

    private final static Map<String, String> vStringsMap = new HashMap<>();
    private final static Map<String, String> vCharsMap = new HashMap<>();
    private final static Map<String, String> vLineCommentsMap = new HashMap<>();
    private final static Map<String, String> vBlockCommentsMap = new HashMap<>();

    public static String processJavaCodePrepare(String text) {
        text = processStringsPrepare(text);
        text = processLineCommentsPrepare(text);
        text = processBlockCommentsPrepare(text);
        text = processChars(text);
        return text;
    }

    public static String processJavaCodeComplete(String text) {
        text = processStringsComplete(text);
        text = completeWithMap(text, vLineCommentsMap);
        text = completeWithMap(text, vBlockCommentsMap);
        text = completeWithMap(text, vCharsMap);

        text = text.replace("##CHR_N##", "'\\n'");
        text = text.replace("##STR_N##", "\\\\n");

        text = text.replace("#lt#", "<");
        text = text.replace("#gt#", ">");

        return text;
    }

    public static String processStringsPrepare(String text) {
        text = text.replace("\r\n", "\n");

        text = text.replace("<", "#lt#");
        text = text.replace(">", "#gt#");
        text = text.replace("'\\n'", "##CHR_N##");
        text = text.replace("\\\\n", "##STR_N##");

        text = text.replace("https://", "##HTTPS##");
        text = text.replace("http://", "##HTTP##");

        //text = text.replace(" \"\\\\\" ", "##ESC_01##");
        //text = text.replace("\\\";", "##ESC_QUOTE_SEMIC##");
        //text = text.replace("\\\"", "##ESC_QUOTE##");
        text = text.replace("'\\''", "##ESC_CHR_QUOTE##");
        //text = text.replace("##ESC_QUOTE_SEMIC##", "\\\";");
        //text = text.replace(":", "##COLON##");

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
                if (indexStart < 0) continue;

                indexEnd = line.indexOf('"', indexStart + 1);
                if (indexEnd <= indexStart) continue;

                do {
                    if (indexEnd >= 2 && line.charAt(indexEnd - 1) == '\\' && line.charAt(indexEnd - 2) != '\\') {
                        indexEnd = line.indexOf('"', indexEnd + 1);
                    } else {
                        break;
                    }
                } while (indexEnd >= indexStart);

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

            } while (indexStart >= 0 && indexEnd > 0 && count < 100000);

            if (i > 0) {
                resultText += '\n';
            }
            resultText += line;
        }

        return resultText;
    }

    public static String processStringsComplete(String text) {
        text = completeWithMap(text, vStringsMap);

        text = text.replace("##HTTPS##", "https://");
        text = text.replace("##HTTP##", "http://");
        //text = text.replace("##ESC_01##", " \"\\\\\" ");
        //text = text.replace("##ESC_QUOTE##", "\\\"");
        //text = text.replace("##ESC_CHR_QUOTE##", "'\\''");
        //text = text.replace("##COLON##", ":");

        return text;
    }

    private static String completeWithMap(String text, Map<String, String> map) {
        for (Map.Entry<String, String> mapElem : map.entrySet()) {
            text = text.replace(mapElem.getKey(), mapElem.getValue());
        }
        return text;
    }

    private static String processChars(String text) {
        var indexStart = 0;
        var indexEnd = 0;
        var resultText = "";

        var count = 100;
        String mapElemKey;
        String strOrigin;
        String line;

        String[] lines = text.split("\n");

        // process 'a' - common chars
        for (var i = 0; i < lines.length; i++) {
            line = lines[i];

            do {
                indexStart = line.indexOf("'");
                if (indexStart < 0) continue;

                indexEnd = line.indexOf("'", indexStart + 1);
                if (indexEnd <= indexStart) continue;

                count++;

                strOrigin = line.substring(indexStart, indexEnd + 1);

                mapElemKey = "#CHR" + count + '#';
                vCharsMap.put(mapElemKey, strOrigin);
                println(">> " + mapElemKey + " = " + strOrigin);

                line = line.substring(0, indexStart)
                        + mapElemKey
                        + line.substring(indexEnd + 1);

            } while (indexStart >= 0 && indexEnd > 0 && count < 100000);

            if (i > 0) {
                resultText += '\n';
            }
            resultText += line;
        }

        return resultText;
    }

    public static String processLineCommentsPrepare(String text) {
        String[] lines = text.split("\n");
        int index;
        String resultText = "";

        int count = 100;
        String mapElemKey;
        String strComment, strTextAfter;
        boolean isFullLineComment;

        for (var i = 0; i < lines.length; i++) {
            isFullLineComment = false;

            index = lines[i].indexOf("//");
            if (index >= 0) {
                count++;

                isFullLineComment = lines[i].substring(0, index).isBlank();

                strComment = lines[i].substring(index);
                if (isFullLineComment) {
                    strTextAfter = "\n" + lines[i];
                } else {
                    strTextAfter = strComment;
                }

                mapElemKey = "#CMTL" + count + '#';
                vLineCommentsMap.put(mapElemKey, strTextAfter);
                println(">> " + mapElemKey + " = " + strTextAfter);

                if (isFullLineComment) {
                    lines[i] = mapElemKey;
                } else {
                    lines[i] = lines[i].substring(0, index) + mapElemKey;
                }
            }

            if (i > 0 && !isFullLineComment) {
                resultText += '\n';
            }
            resultText += lines[i];
        }

        return resultText;
    }

    public static String processBlockCommentsPrepare(String text) {
        var indexStart = 0;
        var indexEnd = 0;
        var count = 0;
        String strComment, strTextAfter;
        String mapElemKey;

        String textBeforeComment;
        int nIndexBeforeComment;
        boolean isFirstCmtLineEmpty;
        do {
            indexStart = text.indexOf("/*", indexStart);
            if (indexStart >= 0) {
                indexEnd = text.indexOf("*/", indexStart + 1);
                if (indexEnd > indexStart) {
                    count++;

                    textBeforeComment = text.substring(0, indexStart);
                    nIndexBeforeComment = textBeforeComment.lastIndexOf('\n');
                    textBeforeComment = textBeforeComment.substring(nIndexBeforeComment + 1, indexStart);
                    isFirstCmtLineEmpty = textBeforeComment.isBlank();

                    strComment = text.substring(indexStart, indexEnd + 2);

                    if (isFirstCmtLineEmpty) {
                        strTextAfter = "\n" + textBeforeComment + strComment;
                    } else {
                        strTextAfter = strComment;
                    }

                    mapElemKey = "#CMTB" + (100 + count) + '#';
                    vBlockCommentsMap.put(mapElemKey, strTextAfter);
                    println(">> " + mapElemKey + " = " + strTextAfter);

                    if (isFirstCmtLineEmpty) {
                        text = text.substring(0, Math.max(0, nIndexBeforeComment))
                                + mapElemKey
                                + text.substring(Math.min(text.length(), indexEnd + 2));
                    } else {
                        text = text.substring(0, Math.max(0, indexStart))
                                + mapElemKey
                                + text.substring(Math.min(text.length(), indexEnd + 2));
                    }
                    indexStart = indexEnd + 2 + (mapElemKey.length() - strComment.length());
                }
            }
        } while (indexStart >= 0 && indexEnd > 0 && count < 1000);
        return text;
    }
}

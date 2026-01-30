package com.zmpc.process;

import com.zmpc.common.Str;
import com.zmpc.common.StrBuilder;
import com.zmpc.entity.JavaFileParsingResult;

import java.util.HashMap;
import java.util.Map;

import static com.zmpc.common.Print.println;
import static com.zmpc.common.Str.*;

public class JavaCodeProcessor {

    private final static Map<String, String> vStringsMap = new HashMap<>();
    private final static Map<String, String> vCharsMap = new HashMap<>();
    private final static Map<String, String> vLineCommentsMap = new HashMap<>();
    private final static Map<String, String> vBlockCommentsMap = new HashMap<>();

    public static JavaFileParsingResult processJavaCodePrepare(Str text) {
        var result = new JavaFileParsingResult(text);
        processStringsPrepare(text);
        processLineCommentsPrepare(text);
        processBlockCommentsPrepare(text);
        processChars(text);
        result.setText(text);
        return result;
    }

    public static void processJavaCodeComplete(Str text) {
        processStringsComplete(text);
        completeWithMap(text, vLineCommentsMap);
        completeWithMap(text, vBlockCommentsMap);
        completeWithMap(text, vCharsMap);

        text.replace("##CHR_N##", "'\\n'");
        text.replace("##STR_N##", "\\\\n");

        text.replace("#lt#", "<");
        text.replace("#gt#", ">");
    }

    public static void processStringsPrepare(Str text) {
        text.replace("\r\n", "\n");

        text.replace("<", "#lt#");
        text.replace(">", "#gt#");
        text.replace("'\\n'", "##CHR_N##");
        text.replace("\\\\n", "##STR_N##");

        text.replace("https://", "##HTTPS##");
        text.replace("http://", "##HTTP##");

        //text.replace(" \"\\\\\" ", "##ESC_01##");
        //text.replace("\\\";", "##ESC_QUOTE_SEMIC##");
        //text.replace("\\\"", "##ESC_QUOTE##");
        text.replace("'\\''", "##ESC_CHR_QUOTE##");
        //text.replace("##ESC_QUOTE_SEMIC##", "\\\";");
        //text.replace(":", "##COLON##");

        // process """str""" - block strings
        processBlockStrings(text);

        // process "str" - common strings
        processLineStrings(text);
    }

    private static void processBlockStrings(Str text) {
        int indexStart = 0;
        int indexEnd = 0;

        int count = 10;
        String mapElemKey;
        Str strOrigin;

        do {
            indexStart = indx(text, "\"\"\"", indexStart);
            if (indexStart >= 0) {
                indexEnd = indx(text, "\"\"\"", indexStart + 3);
                if (indexEnd > indexStart) {
                    count++;

                    strOrigin = subs(text, indexStart, indexEnd + 3);

                    mapElemKey = "#STRB" + count + "#";
                    vStringsMap.put(mapElemKey, strOrigin.str());
                    println(">> " + mapElemKey + " = " + strOrigin);

                    text.set(subs(text, 0, indexStart).str()
                            + mapElemKey
                            + subs(text, indexEnd + 3));

                    indexStart = indexEnd + 3 + (mapElemKey.length() - strOrigin.len());
                }
            }
        } while (indexStart >= 0 && indexEnd > 0 && count < 10000);
    }

    private static void processLineStrings(Str text) {
        int indexStart = 0;
        int indexEnd = 0;
        var resultText = new StrBuilder();

        var count = 100;
        String mapElemKey;
        Str strOrigin;
        Str line;

        String[] lines = text.split("\n");
        int commentIndex;
        // process "str" - common strings
        for (var i = 0; i < lines.length; i++) {
            line = str(lines[i]);

            do {
                indexStart = indx(line, '"');
                if (indexStart < 0) continue;

                indexEnd = indx(line, '"', indexStart + 1);
                if (indexEnd <= indexStart) continue;

                do {
                    if (indexEnd >= 2 && line.charAt(indexEnd - 1) == '\\' && line.charAt(indexEnd - 2) != '\\') {
                        indexEnd = indx(line, '"', indexEnd + 1);
                    } else {
                        break;
                    }
                } while (indexEnd >= indexStart);

                commentIndex = indx(line, "//");
                if (commentIndex >= 0 && commentIndex < indexStart) {
                    // don't process strings after comment: //
                    break;
                }

                count++;

                strOrigin = subs(line, indexStart, indexEnd + 1);

                mapElemKey = "#STR" + count + '#';
                vStringsMap.put(mapElemKey, strOrigin.str());
                println(">> " + mapElemKey + " = " + strOrigin);

                line.set(subs(line, 0, indexStart)
                        + mapElemKey
                        + subs(line, indexEnd + 1));

            } while (indexStart >= 0 && indexEnd > 0 && count < 100000);

            if (i > 0) {
                resultText.add("\n");
            }
            resultText.add(line);
        }

        text.set(resultText);
    }

    public static void processStringsComplete(Str text) {
        completeWithMap(text, vStringsMap);

        text.replace("##HTTPS##", "https://");
        text.replace("##HTTP##", "http://");
        //text.replace("##ESC_01##", " \"\\\\\" ");
        //text.replace("##ESC_QUOTE##", "\\\"");
        text.replace("##ESC_CHR_QUOTE##", "'\\''");
        //text.replace("##COLON##", ":");
    }

    private static void completeWithMap(Str text, Map<String, String> map) {
        for (Map.Entry<String, String> mapElem : map.entrySet()) {
            text.replace(mapElem.getKey(), mapElem.getValue());
        }
    }

    private static void processChars(Str text) {
        int indexStart = 0;
        int indexEnd = 0;
        var resultText = new StrBuilder();

        int count = 100;
        String mapElemKey;
        Str strOrigin;
        Str line;

        String[] lines = text.split("\n");

        // process 'a' - common chars
        for (var i = 0; i < lines.length; i++) {
            line = str(lines[i]);

            do {
                indexStart = indx(line, "'");
                if (indexStart < 0) continue;

                indexEnd = indx(line, "'", indexStart + 1);
                if (indexEnd <= indexStart) continue;

                count++;

                strOrigin = subs(line, indexStart, indexEnd + 1);

                mapElemKey = "#CHR" + count + '#';
                vCharsMap.put(mapElemKey, strOrigin.str());
                println(">> " + mapElemKey + " = " + strOrigin);

                line.set(subs(line, 0, indexStart)
                        + mapElemKey
                        + subs(line, indexEnd + 1));

            } while (indexStart >= 0 && indexEnd > 0 && count < 100000);

            if (i > 0) {
                resultText.add("\n");
            }
            resultText.add(line);
        }

        text.set(resultText);
    }

    public static void processLineCommentsPrepare(Str text) {
        Str line;
        String[] lines = text.split("\n");
        int index;
        var resultText = new StrBuilder();

        int count = 100;
        String mapElemKey;
        Str strComment, strTextAfter;
        boolean isFullLineComment;

        for (var i = 0; i < lines.length; i++) {
            line = str(lines[i]);
            isFullLineComment = false;

            index = indx(line, "//");
            if (index >= 0) {
                count++;

                isFullLineComment = subs(line, 0, index).blank();

                strComment = subs(line, index);
                if (isFullLineComment) {
                    strTextAfter = str("\n" + line);
                } else {
                    strTextAfter = strComment;
                }

                mapElemKey = "#CMTL" + count + '#';
                vLineCommentsMap.put(mapElemKey, strTextAfter.str());
                println(">> " + mapElemKey + " = " + strTextAfter);

                if (isFullLineComment) {
                    line.set(mapElemKey);
                } else {
                    line.set(subs(line, 0, index) + mapElemKey);
                }
            }

            if (i > 0 && !isFullLineComment) {
                resultText.add("\n");
            }
            resultText.add(line);
        }

        text.set(resultText);
    }

    public static void processBlockCommentsPrepare(Str text) {
        int indexStart = 0;
        int indexEnd = 0;
        int count = 0;
        Str strComment, strTextAfter;
        String mapElemKey;

        Str textBeforeComment;
        int nIndexBeforeComment;
        boolean isFirstCmtLineEmpty;
        do {
            indexStart = indx(text, "/*", indexStart);
            if (indexStart >= 0) {
                indexEnd = indx(text, "*/", indexStart + 1);
                if (indexEnd > indexStart) {
                    count++;

                    textBeforeComment = text.substring(0, indexStart);
                    nIndexBeforeComment = lastIndx(textBeforeComment, "\n");
                    textBeforeComment = subs(textBeforeComment, nIndexBeforeComment + 1, indexStart);
                    isFirstCmtLineEmpty = textBeforeComment.blank();

                    strComment = text.substring(indexStart, indexEnd + 2);

                    if (isFirstCmtLineEmpty) {
                        strTextAfter = str("\n" + textBeforeComment + strComment);
                    } else {
                        strTextAfter = strComment;
                    }

                    mapElemKey = "#CMTB" + (100 + count) + '#';
                    vBlockCommentsMap.put(mapElemKey, strTextAfter.str());
                    println(">> " + mapElemKey + " = " + strTextAfter);

                    if (isFirstCmtLineEmpty) {
                        text.set(subs(text, 0, Math.max(0, nIndexBeforeComment))
                                + mapElemKey
                                + subs(text, Math.min(text.len(), indexEnd + 2)));
                    } else {
                        text.set(subs(text, 0, Math.max(0, indexStart))
                                + mapElemKey
                                + subs(text, Math.min(text.len(), indexEnd + 2)));
                    }
                    indexStart = indexEnd + 2 + (mapElemKey.length() - strComment.len());
                }
            }
        } while (indexStart >= 0 && indexEnd > 0 && count < 1000);
    }

    private static Str processPackageName(Str text) {
        // TODO
        return text;
    }
}

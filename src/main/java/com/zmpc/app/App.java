package com.zmpc.app;

import com.zmpc.util.FileUtils;

import java.io.IOException;

import static com.zmpc.common.Print.println;
import static com.zmpc.process.JavaCodeProcessor.processJavaCodeComplete;
import static com.zmpc.process.JavaCodeProcessor.processJavaCodePrepare;
import static com.zmpc.util.FileUtils.readTextFromFile;

public class App {
    public static void main(String[] args) throws IOException {
        String originFilePath = "D:\\Workspace\\Java\\2025\\Solutions\\JavaCodeSmartTracer\\tracer\\origin\\App.java";
        String outFilePath = "D:\\Workspace\\Java\\2025\\Solutions\\JavaCodeSmartTracer\\tracer\\out\\App.java";

        String content = readTextFromFile(originFilePath);
        content = processJavaCodePrepare(content);
        println(content);
        FileUtils.writeTextToFile(content, outFilePath + ".process.txt");
        content = processJavaCodeComplete(content);
        println("----------------------------------------------");
        println(content);
        FileUtils.writeTextToFile(content, outFilePath);
    }
}

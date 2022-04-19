package com.gaofeigr.test;


import com.gaofeigr.dialogs.ExportDialog;
import com.gaofeigr.utils.FileUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.sun.jna.platform.FileUtils;

import javax.tools.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MyTest {
    public static void main(String[] args) throws Exception {
        testDialog();
    }

    public static void testDialog1() {
    }

    public static void testDeleteFile() {
        try {
            FileUtil.deleteDir(new File("C:\\Users\\gaofe\\Desktop\\新建文件夹\\新建文本文档.txt"));

        } catch (Exception e) {
            System.err.println("FileUtil.createFile()---------->error");
            e.toString();
        }
    }

    public static void testDialog() throws IOException {
        ExportDialog dialog = new ExportDialog();
        dialog.pack();
        dialog.setTitle("ExportFiles");
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    public static void testCompiler() throws Exception {
        String sOutputPath = "";
        List<String> paths = new ArrayList<String>();
        List<String> options = new ArrayList<String>();
        paths.add("");
//        options.add("-encoding");
//        options.add("UTF8");
//        options.add("-verbose");
//        options.add("-classpath");
//        options.add("-cp");
        options.add(buildClassPath(".","D:\\_WORK\\ekp\\src\\","D:\\_WORK\\ekp\\WebContent\\WEB-INF\\lib\\*","C:\\DevelopmentTools\\apache-tomcat-7.0.75\\lib\\*"));
//        options.add(".;D:\\_WORK\\ekp\\src\\;D:\\_WORK\\ekp\\WebContent\\WEB-INF\\lib\\*;C:\\DevelopmentTools\\apache-tomcat-7.0.75\\lib\\*");
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);
        JavaFileManager.Location oLocation = StandardLocation.CLASS_OUTPUT;
        fileManager.handleOption("-classpath", options.iterator());
        fileManager.setLocation(oLocation, Arrays.asList(new File[] { new File(sOutputPath) }));
        Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromStrings(paths);
        List<String> test = new ArrayList<String>();
        test.add("-source");
        test.add("1.6");
        test.add("-target");
        test.add("1.6");
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, test, null, compilationUnits);
        if (task.call()) {
            System.out.println(true);
        } else {
            List<Diagnostic<? extends JavaFileObject>> diagnosticList = diagnostics.getDiagnostics();
            for (Diagnostic<? extends JavaFileObject> diagnostic : diagnosticList) {
                diagnostic.getKind();
                if (diagnostic.getKind().equals(Diagnostic.Kind.ERROR)) {
                    throw new Exception(diagnostic.getMessage(null));
                }
            }
            System.out.println(false);
        }
        fileManager.close();
    }

    private static String buildClassPath(String... paths) {
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            if (path.endsWith("*")) {
                path = path.substring(0, path.length() - 1);
                File pathFile = new File(path);
                for (File file : pathFile.listFiles()) {
                    if (file.isFile() && file.getName().endsWith(".jar")) {
                        sb.append(path);
                        sb.append(file.getName());
                        sb.append(System.getProperty("path.separator"));
                    }
                }
            } else {
                sb.append(path);
                sb.append(System.getProperty("path.separator"));
            }
        }
        return sb.toString();
    }
}
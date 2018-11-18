package com.gaofeigr.test;


import javax.tools.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyTest {
    public static void main(String[] args) throws Exception {
        test1();
    }

    public static void test1() throws Exception {
        String sOutputPath = "D:\\_WORK\\ekp\\src\\com\\landray\\kmss\\sys\\news\\actions\\";
        List<String> paths = new ArrayList<String>();
        List<String> options = new ArrayList<String>();
//        paths.add("D:\\IDEAPluginDev\\Export\\src\\com\\gaofeigr\\actions\\ExportFilesAction.java");
        paths.add("D:\\_WORK\\ekp\\src\\com\\landray\\kmss\\sys\\news\\actions\\SysNewsConfigAction.java");
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
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
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
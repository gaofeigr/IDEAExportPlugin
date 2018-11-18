package com.gaofeigr.utils;

import com.intellij.xml.actions.xmlbeans.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 常用的文件操作工具类
 */
public class FileUtil {
    private FileUtil(){}

    /**
     * 根据给定路径创建文件，若给定路径不存在，则不做操作
     * @param path
     */
    public static void createFolder(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            System.err.println("FileUtil.createFile()---------->error");
            e.toString();
        }
    }

    /**
     * 根据给定路径复制文件(classpath中其他类型的文件)
     * @param fromPath
     * @param toPath
     */
    public static void copyOtherFiles(List<String> fromPath, String fromPrefix, String toPath) {
        for (String path : fromPath) {
            File fromFile = new File(path);
            File toFile = new File(toPath + path.replaceAll(fromPrefix.replaceAll("\\\\", "/"), ""));
            FileUtils.copyFile(fromFile, toFile);
        }
    }

    /**
     * 根据给定路径复制文件(资源文件)
     * @param fromPath
     * @param toPath
     */
    public static void copyResourceFiles(List<String> fromPath, String fromPrefix, String toPath) {
        for (String path : fromPath) {
            String tempToPath = toPath + path.replaceAll(fromPrefix.replaceAll("\\\\", "/"), "");
            File fromFile = new File(path);
            File toFile = new File(tempToPath.replaceAll("/", "\\\\"));
            if (!toFile.getParentFile().exists()) {
                toFile.getParentFile().mkdirs();
            }
            FileUtils.copyFile(fromFile, toFile);
        }
    }
}
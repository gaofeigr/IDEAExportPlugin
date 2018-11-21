package com.gaofeigr.utils;

import com.intellij.xml.actions.xmlbeans.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 常用的文件操作工具类
 */
public class FileUtil {
    private FileUtil() {
    }

    /**
     * 根据给定路径创建文件
     *
     * @param path
     */
    public static void createFolder(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                FileUtil.deleteDir(file);
            }
            file.mkdirs();
        } catch (Exception e) {
            System.err.println("FileUtil.createFile()---------->error");
            e.toString();
        }
    }

    /**
     * 根据给定路径复制文件(classpath中其他类型的文件)
     *
     * @param fromPath
     * @param toPath
     */
    public static int copyOtherFiles(List<String> fromPath, String fromPrefix, String toPath) {
        int result = 0;
        for (String path : fromPath) {
            File fromFile = new File(path);
            File toFile = new File(toPath + path.replaceAll(fromPrefix.replaceAll("\\\\", "/"), ""));
            if (FileUtils.copyFile(fromFile, toFile)) {
                result++;
            }
        }
        return result;
    }

    /**
     * 根据给定路径复制文件(资源文件)
     *
     * @param fromPath
     * @param toPath
     */
    public static int copyResourceFiles(List<String> fromPath, String fromPrefix, String toPath) {
        int result = 0;
        for (String path : fromPath) {
            String tempToPath = toPath + path.replaceAll(fromPrefix.replaceAll("\\\\", "/"), "");
            File fromFile = new File(path);
            File toFile = new File(tempToPath.replaceAll("/", "\\\\"));
            if (!toFile.getParentFile().exists()) {
                toFile.getParentFile().mkdirs();
            }
            if (FileUtils.copyFile(fromFile, toFile)) {
                result++;
            }
        }
        return result;
    }

    /**
     * 删除空目录
     */
    public static void doDeleteEmptyDir(String dir) throws IOException {
        boolean success = (new File(dir)).delete();
        if (!success) {
            throw new IOException("删除目录失败");
        }
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
package com.gaofeigr.utils;

import java.io.File;
import java.util.List;

/**
 * 常用String操作工具类
 */
public class StringUtils {

    private StringUtils(){}

    /**
     * 构建引用的jar包目录
     * @param paths
     * @return
     */
    public static String buildClassPath(List<String> paths) {
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
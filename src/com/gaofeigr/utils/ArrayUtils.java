package com.gaofeigr.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 常用集合操作工具类
 */
public class ArrayUtils {
    private ArrayUtils(){}

    public static boolean isEmpty(List list) {
        boolean result;
        if (list != null) {
            result = list.size() == 0;
        } else {
            result = true;
        }
        return result;
    }

    public static boolean isNotEmpty(List list) {
        return !isEmpty(list);
    }
}
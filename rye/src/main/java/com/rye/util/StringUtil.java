package com.rye.util;

public class StringUtil {

    private static final String EMPTY_STRING = "";

    public static String nullToEmpty(String str) {
        return str == null ? EMPTY_STRING : str;
    }

    public static boolean isBlank(String str) {
        return str == null || EMPTY_STRING.equals(str.trim()) || EMPTY_STRING.equals(str.strip());
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static boolean isAllNotBlank(String... strs) {
        for (String str : strs) {
            if (isBlank(str)) {
                return false;
            }
        }
        return true;
    }

    public static String trimPrefix(String str, String prefix) {
        return trim(str, prefix, true, false);
    }

    public static String trimSuffix(String str, String suffix) {
        return trim(str, suffix, false, true);
    }

    public static String trim(String str, String preOrSuffix) {
        return trim(str, preOrSuffix, true, true);
    }

    public static String trim(String str, String preOrSuffix, boolean isPrefix, boolean isSuffix) {
        if (isAllNotBlank(str, preOrSuffix)) {
            while (str.startsWith(preOrSuffix) && isPrefix) {
                str = str.substring(str.indexOf(preOrSuffix) + preOrSuffix.length());
            }
            while (str.endsWith(preOrSuffix) && isSuffix) {
                str = str.substring(0, str.lastIndexOf(preOrSuffix));
            }
        }
        return str;
    }

    public static String toString(Object object) {
        return object != null ? object.toString() : "null";
    }
}

package com.sherry.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");

    private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");

    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }
        return "".equals(str.trim());
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static String toLowerCaseFirstOne(String str) {
        if (isBlank(str)) {
            return str;
        }
        if (Character.isLowerCase(str.charAt(0))) {
            return str;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    public static String toUpperCaseFirstOne(String str) {
        if (isBlank(str)) {
            return str;
        }
        if (Character.isUpperCase(str.charAt(0))) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String lineToHump(String str) {
        if (isBlank(str)) {
            return str;
        }
        Matcher matcher = LINE_PATTERN.matcher(str.toLowerCase());
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        result = result.substring(0, 1).toUpperCase() + result.substring(1);
        return result;
    }

    public static String humpToLine(String str) {
        if (isBlank(str)) {
            return str;
        }
        Matcher matcher = HUMP_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        String result = sb.toString();
        if (result.startsWith("_")) {
            return result.substring(1);
        } else {
            return result;
        }
    }

    public static String underlineToHorizontal(String str) {
        if (isBlank(str)) {
            return str;
        }
        return str.replace("_", "-");
    }

    public static String packageNameToPath(String packageName) {
        if (isBlank(packageName)) {
            return packageName;
        }
        return packageName.replace(".", "/");
    }

}
package com.sherry.util;

import java.io.InputStream;

public class FileUtil {

    public static InputStream getClasspathResource(String filename) {
        return FileUtil.class.getClassLoader().getResourceAsStream(filename);
    }
}
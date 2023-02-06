package com.rye.util;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarFile;

public class URLUtil {

    public static File getFile(URL url) {
        if (url != null) {
            try {
                URI uri = url.toURI();
                return new File(uri);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException("Url is null");
    }

    public static JarFile getJarFile(URL url) {
        try {
            JarURLConnection urlConnection = (JarURLConnection) url.openConnection();
            return urlConnection.getJarFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

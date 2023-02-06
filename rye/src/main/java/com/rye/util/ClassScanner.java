package com.rye.util;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassScanner {

    private static final String CLASS_PROTOCOL = "file";
    private static final String CLASS_SUFFIX = ".class";
    private static final String JAR_PROTOCOL = "jar";
    private static final String JAR_SUFFIX = ".jar";
    private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();

    private final String packagePath;
    private final String packageDirName;
    private final Function<Class<?>, Boolean> classFilter;
    private final Set<Class<?>> classes;

    public ClassScanner(String packageName) {
        this(packageName, null);
    }

    public ClassScanner(String packageName, Function<Class<?>, Boolean> classFilter) {
        packageName = StringUtil.nullToEmpty(packageName);
        this.packagePath = packageName.replace(".", "/");
        this.packageDirName = packageName.replace(".", File.separator);
        this.classFilter = classFilter;
        this.classes = new HashSet<>();
    }

    public Set<Class<?>> scan() {
        try {
            Enumerations<URL> enumerations = new Enumerations<>(CLASS_LOADER.getResources(this.packagePath));
            for (URL url : enumerations) {
                String protocol = url.getProtocol();
                switch (protocol) {
                    case CLASS_PROTOCOL:
                        scanFile(URLUtil.getFile(url), null);
                        break;
                    case JAR_PROTOCOL:
                        scanJar(URLUtil.getJarFile(url));
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return classes;
    }

    private void scanFile(File file, String root) {
        if (file.isFile()) {
            String path = file.getAbsolutePath();
            if (path.endsWith(CLASS_SUFFIX)) {
                path = path.substring(root.length());
                path = StringUtil.trim(path, File.separator, true, true);
                path = StringUtil.trimSuffix(path, CLASS_SUFFIX);
                String className = path.replace(File.separator, ".");
                add(className);
            } else if (path.endsWith(JAR_SUFFIX)) {
                try {
                    scanJar(new JarFile(file));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    if (root == null) {
                        root = StringUtil.trimSuffix(file.getAbsolutePath(), packageDirName);
                    }
                    scanFile(subFile, root);
                }
            }
        }
    }

    private void scanJar(JarFile jarFile) {
        Enumerations<JarEntry> enumerations = new Enumerations<>(jarFile.entries());
        for (JarEntry jarEntry : enumerations) {
            String filename = StringUtil.trimPrefix(jarEntry.getName(), "/");
            if (filename.startsWith(packagePath + "/") && filename.endsWith(CLASS_SUFFIX) && !jarEntry.isDirectory()) {
                String className = StringUtil.trimSuffix(filename, CLASS_SUFFIX).replace('/', '.');
                add(className);
            }
        }
    }

    private void add(String name) {
        Class<?> cls = loadClass(name);
        boolean hasClass = cls != null;
        boolean isValid = classFilter == null || Objects.requireNonNullElse(classFilter.apply(cls), Boolean.FALSE);
        if (hasClass && isValid) {
            classes.add(cls);
        }
    }

    private Class<?> loadClass(String name) {
        try {
            return Class.forName(name, false, CLASS_LOADER);
        } catch (NoClassDefFoundError e) {
            // 由于依赖库导致的类无法加载，直接跳过此类
        } catch (UnsupportedClassVersionError e) {
            // 版本导致的不兼容的类，跳过
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}

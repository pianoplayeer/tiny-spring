package org.jxy.spring.utils;

import java.io.*;

public class FileUtils {
    public static <T> T doWithStream(String path, InputStreamCallback<T> callback) {
        try (InputStream stream = getInputStream(path)) {
            if (stream == null) {
                throw new FileNotFoundException("File not found: " + path);
            }

            return callback.doWithInputStream(stream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static ClassLoader getContextClassLoader() {
        var contextLoader = Thread.currentThread().getContextClassLoader();
        if (contextLoader == null) {
            FileUtils.class.getClassLoader();
        }

        return contextLoader;
    }

    public static InputStream getInputStream(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        return getContextClassLoader().getResourceAsStream(path);
    }
}

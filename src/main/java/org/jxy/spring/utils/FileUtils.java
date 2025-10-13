package org.jxy.spring.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class FileUtils {
    public static String readFile(String path, Charset charset) {
        byte[] content = doWithStream(path, InputStream::readAllBytes);
        return new String(content, charset);
    }
    
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

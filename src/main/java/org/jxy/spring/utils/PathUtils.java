package org.jxy.spring.utils;

import java.util.regex.Pattern;

import jakarta.servlet.ServletException;

public class PathUtils {
    public static Pattern compile(String path) throws ServletException {
        String regPath = path.replace("\\{[a-zA-Z][a-zA-Z0-9]*\\}", "(?<$1>[^/]+)");
        if (regPath.indexOf('{') >= 0 || regPath.indexOf('}') >= 0) {
            throw new ServletException("Invalid path: " + path);
        }

        return Pattern.compile("^" + regPath + "$");
    }
}

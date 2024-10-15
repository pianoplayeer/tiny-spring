package org.jxy.spring.ioc.resolver;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@AllArgsConstructor
public class ResourceResolver {
    private String basePackage;

    public <R> List<R> scan(Function<Resource, R> mapper) throws IOException, URISyntaxException {
        String basePath = basePackage.replace(".", "/");
        return doScan(basePath, mapper);
    }

    private <R> List<R> doScan(String basePath, Function<Resource, R> mapper)
            throws IOException, URISyntaxException {
        log.info("scan resources: {}", basePath);
        var urls = findResourcesURL(basePath);
        List<R> resources = new ArrayList<>();

        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            URI uri = url.toURI();
            String uriStr = removeTrailSlash(uriToString(uri));
            String uriBase = uriStr.substring(0, uriStr.length() - basePath.length());

            if (uriStr.startsWith("file:")) {
                uriBase = uriBase.substring(5);
                resources.addAll(scanFile(false, uriBase, Paths.get(uri), mapper));
            } else {
                resources.addAll(scanFile(true, uriStr, getJarPath(uri, basePath), mapper));
            }
        }

        return resources;
    }

    private <R> List<R> scanFile(boolean isJar, String base, Path path, Function<Resource, R> mapper) throws IOException {
        String basePath = removeTrailSlash(base);
        List<R> resources = new ArrayList<>();

        Files.walk(path).filter(Files::isRegularFile).forEach(file -> {
            log.info("scan file: {}", file.toString());
            Resource res = null;
            if (isJar) {
                res = new Resource(basePath, removeHeadSlash(file.toString()));
            } else {
                String filePath = file.toString();
                res = new Resource("file:" + basePath, removeHeadSlash(filePath.substring(basePath.length())));
            }

            R r = mapper.apply(res);
            if (r != null) {
                resources.add(r);
            }
        });

        return resources;
    }

    private Path getJarPath(URI jarUri, String base) throws IOException {
        return FileSystems.newFileSystem(jarUri, Map.of()).getPath(base);
    }

    private String removeHeadSlash(String uri) {
        if (uri.startsWith("/") || uri.startsWith("\\")) {
            return uri.substring(1);
        }
        return uri;
    }

    private String removeTrailSlash(String uri) {
        if (uri.endsWith("/") || uri.endsWith("\\")) {
            return uri.substring(0, uri.length() - 1);
        }
        return uri;
    }

    private String uriToString(URI uri) {
        return URLDecoder.decode(uri.toString(), StandardCharsets.UTF_8);
    }

    private Enumeration<URL> findResourcesURL(String basePath) throws IOException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = getClass().getClassLoader();
        }

        return cl.getResources(basePath);
    }
}

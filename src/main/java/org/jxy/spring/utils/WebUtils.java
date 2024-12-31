package org.jxy.spring.utils;

import lombok.extern.slf4j.Slf4j;
import org.jxy.spring.ioc.resolver.PropertyResolver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class WebUtils {
    public static final String REQUEST_PARAM_DEFAULT = "\0\0\t\n\n\0\r\r";

    private static final String CONFIG_APP_YML = "/application.yml";

    private static final String CONFIG_APP_PROP = "/application.properties";

    public static PropertyResolver createPropertyResolver() {
        Properties props = new Properties();
        try (InputStream inputStream = FileUtils.getInputStream(CONFIG_APP_YML)) {
            Map<String, Object> ymlMap = YmlUtils.loadYmlAsPlainMap(inputStream);
            log.info("load config {}", CONFIG_APP_YML);

            props.putAll(ymlMap);
        } catch (IOException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                FileUtils.doWithStream(CONFIG_APP_PROP, stream -> {
                    props.load(stream);
                    log.info("load config {}", CONFIG_APP_PROP);

                    return true;
                });
            }
        }

        return new PropertyResolver(props);
    }
}

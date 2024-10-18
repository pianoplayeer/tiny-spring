package org.jxy.spring.ioc.scan.destroy;

import jakarta.annotation.PreDestroy;
import org.jxy.spring.ioc.annotation.Component;
import org.jxy.spring.ioc.annotation.Value;

@Component
public class AnnotationDestroyBean {

    @Value("${app.title}")
    public String appTitle;

    @PreDestroy
    void destroy() {
        this.appTitle = null;
    }
}

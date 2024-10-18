package org.jxy.spring.ioc.scan.init;

import jakarta.annotation.PostConstruct;
import org.jxy.spring.ioc.annotation.Component;
import org.jxy.spring.ioc.annotation.Value;

@Component
public class AnnotationInitBean {

    @Value("${app.title}")
    String appTitle;

    @Value("${app.version}")
    String appVersion;

    public String appName;

    @PostConstruct
    void init() {
        this.appName = this.appTitle + " / " + this.appVersion;
    }
}

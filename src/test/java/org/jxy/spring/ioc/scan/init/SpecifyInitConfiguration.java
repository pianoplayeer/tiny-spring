package org.jxy.spring.ioc.scan.init;

import org.jxy.spring.ioc.annotation.Bean;
import org.jxy.spring.ioc.annotation.Configuration;
import org.jxy.spring.ioc.annotation.Value;

@Configuration
public class SpecifyInitConfiguration {

    @Bean(initMethod = "init")
    SpecifyInitBean createSpecifyInitBean(@Value("${app.title}") String appTitle, @Value("${app.version}") String appVersion) {
        return new SpecifyInitBean(appTitle, appVersion);
    }
}

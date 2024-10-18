package org.jxy.spring.ioc.scan.destroy;


import org.jxy.spring.ioc.annotation.Bean;
import org.jxy.spring.ioc.annotation.Configuration;
import org.jxy.spring.ioc.annotation.Value;

@Configuration
public class SpecifyDestroyConfiguration {

    @Bean(destroyMethod = "destroy")
    SpecifyDestroyBean createSpecifyDestroyBean(@Value("${app.title}") String appTitle) {
        return new SpecifyDestroyBean(appTitle);
    }
}

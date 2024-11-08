package org.jxy.spring.ioc.scan.destroy;


import org.jxy.spring.annotation.Bean;
import org.jxy.spring.annotation.Configuration;
import org.jxy.spring.annotation.Value;

@Configuration
public class SpecifyDestroyConfiguration {

    @Bean(destroyMethod = "destroy")
    SpecifyDestroyBean createSpecifyDestroyBean(@Value("${app.title}") String appTitle) {
        return new SpecifyDestroyBean(appTitle);
    }
}

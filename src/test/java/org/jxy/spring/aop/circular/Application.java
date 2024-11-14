package org.jxy.spring.aop.circular;

import org.jxy.spring.annotation.Bean;
import org.jxy.spring.annotation.ComponentScan;
import org.jxy.spring.annotation.Configuration;
import org.jxy.spring.aop.processor.AopAnnotationBeanPostProcessor;

@ComponentScan
@Configuration
public class Application {
    @Bean
    public AopAnnotationBeanPostProcessor aopAnnotationBeanPostProcessor() {
        return new AopAnnotationBeanPostProcessor();
    }
}

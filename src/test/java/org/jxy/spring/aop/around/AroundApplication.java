package org.jxy.spring.aop.around;

import org.jxy.spring.annotation.Bean;
import org.jxy.spring.annotation.ComponentScan;
import org.jxy.spring.annotation.Configuration;
import org.jxy.spring.aop.processor.AopAnnotationBeanPostProcessor;

@Configuration
@ComponentScan
public class AroundApplication {
    @Bean
    public AopAnnotationBeanPostProcessor aopAnnotationBeanPostProcessor() {
        return new AopAnnotationBeanPostProcessor();
    }
}

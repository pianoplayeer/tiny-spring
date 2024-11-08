package org.jxy.spring.ioc.utils;

import org.junit.jupiter.api.Test;
import org.jxy.spring.annotation.Component;
import org.jxy.spring.annotation.Configuration;
import org.jxy.spring.annotation.Order;
import org.jxy.spring.exception.BeanDefinitionException;
import org.jxy.spring.utils.ClassUtil;

import static org.junit.jupiter.api.Assertions.*;

public class AnnoUtilsTest {

    @Test
    public void noComponent() throws Exception {
        assertNull(ClassUtil.findAnnotation(Simple.class, Component.class));
    }

    @Test
    public void simpleComponent() throws Exception {
        assertNotNull(ClassUtil.findAnnotation(SimpleComponent.class, Component.class));
        assertEquals("simpleComponent", ClassUtil.findBeanName(SimpleComponent.class));
    }

    @Test
    public void simpleComponentWithName() throws Exception {
        assertNotNull(ClassUtil.findAnnotation(SimpleComponentWithName.class, Component.class));
        assertEquals("simpleName", ClassUtil.findBeanName(SimpleComponentWithName.class));
    }

    @Test
    public void simpleConfiguration() throws Exception {
        assertNotNull(ClassUtil.findAnnotation(SimpleConfiguration.class, Component.class));
        assertEquals("simpleConfiguration", ClassUtil.findBeanName(SimpleConfiguration.class));
    }

    @Test
    public void simpleConfigurationWithName() throws Exception {
        assertNotNull(ClassUtil.findAnnotation(SimpleConfigurationWithName.class, Component.class));
        assertEquals("simpleCfg", ClassUtil.findBeanName(SimpleConfigurationWithName.class));
    }

    @Test
    public void customComponent() throws Exception {
        assertNotNull(ClassUtil.findAnnotation(Custom.class, Component.class));
        assertEquals("custom", ClassUtil.findBeanName(Custom.class));
    }

    @Test
    public void customComponentWithName() throws Exception {
        assertNotNull(ClassUtil.findAnnotation(CustomWithName.class, Component.class));
        assertEquals("customName", ClassUtil.findBeanName(CustomWithName.class));
    }

    @Test
    public void duplicateComponent() throws Exception {
        assertThrows(BeanDefinitionException.class, () -> {
            ClassUtil.findAnnotation(DuplicateComponent.class, Component.class);
        });
        assertThrows(BeanDefinitionException.class, () -> {
            ClassUtil.findAnnotation(DuplicateComponent2.class, Component.class);
        });
    }
}

@Order(1)
class Simple {
}

@Component
class SimpleComponent {
}

@Component("simpleName")
class SimpleComponentWithName {
}

@Configuration
class SimpleConfiguration {

}

@Configuration("simpleCfg")
class SimpleConfigurationWithName {

}

@CustomComponent
class Custom {

}

@CustomComponent("customName")
class CustomWithName {

}

@Component
@Configuration
class DuplicateComponent {

}

@CustomComponent
@Configuration
class DuplicateComponent2 {

}

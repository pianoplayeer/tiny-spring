package org.jxy.resolver;

import org.junit.Test;
import org.jxy.Main;
import org.jxy.utils.YmlUtil;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class TestResolver {
    @Test
    public void resourceResolveTest() throws IOException, URISyntaxException {
        var resolver = new ResourceResolver("ch.qos");
        resolver.scan(res -> {
            String name = res.name();
            if (name.endsWith(".class")) {
                return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
            }
            return null;
        }).forEach(System.out::println);
    }
    
    @Test
    public void propertyResolveTest() {
        Properties props = new Properties();
        props.setProperty("abc", "def");
        props.setProperty("def", "${abc}");
        props.setProperty("default", "${bbc:bvb}");
        props.setProperty("bbb", "${def:null}");
        
        PropertyResolver resolver = new PropertyResolver(props);
        System.out.println("abc: " + resolver.getProperty("abc"));
        System.out.println("def: " + resolver.getProperty("def"));
        System.out.println("default: " + resolver.getProperty("default"));
        System.out.println("bbb: " + resolver.getProperty("bbb"));
        System.out.println("my: " + resolver.getProperty("${sfdfs:${default}}"));
        System.out.println("error: " + resolver.getProperty("error"));
    }
    
    @Test
    public void ymlLoadTest() {
        var map = YmlUtil.loadYmlAsPlainMap(Main.class.getResourceAsStream("/application.yml"));
        map.forEach((k, v) -> System.out.println(k + ": " + v));
    }
}

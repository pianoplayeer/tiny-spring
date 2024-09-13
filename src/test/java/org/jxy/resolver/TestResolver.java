package org.jxy.resolver;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

public class TestResolver {
    @Test
    public void test() throws IOException, URISyntaxException {
        var resolver = new ResourceResolver("ch.qos");
        resolver.scan(res -> {
            String name = res.name();
            if (name.endsWith(".class")) {
                return name.substring(0, name.length() - 6).replace("/", ".").replace("\\", ".");
            }
            return null;
        }).forEach(System.out::println);
    }
}

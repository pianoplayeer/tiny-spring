package org.jxy;


import org.jxy.resolver.Resource;
import org.jxy.utils.YmlUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(YmlUtil.loadYml(Main.class.getResourceAsStream("/application.yml")));
    }
}
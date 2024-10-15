package org.jxy;


import org.jxy.spring.utils.YmlUtil;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(YmlUtil.loadYml(Main.class.getResourceAsStream("/application.yml")));
    }
}
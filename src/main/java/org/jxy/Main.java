package org.jxy;


import org.jxy.spring.utils.YmlUtil;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(Main.class.getPackage().getName());
        System.out.println(Main.class.getPackageName());
    }
}
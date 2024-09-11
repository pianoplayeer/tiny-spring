package org.jxy;


import org.jxy.resolver.Resource;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var en = Thread.currentThread().getContextClassLoader().getResources("org/jxy/resolver");
        System.out.println(en);
    }
}
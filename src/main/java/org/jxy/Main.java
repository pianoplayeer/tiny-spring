package org.jxy;


import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(Main.class.getPackage().getName());
        System.out.println(Main.class.getPackageName());
    }
}
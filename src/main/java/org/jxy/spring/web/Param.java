package org.jxy.spring.web;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Param {
    String name;

    ParamType paramType;

    Class<?> classType;

    String defaultValue;
}

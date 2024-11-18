package org.jxy.spring.web;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.regex.Pattern;

public class Handler {
    // 是否返回REST:
    boolean isRest;
    // 是否有@ResponseBody:
    boolean isResponseBody;
    // 是否返回void:
    boolean isVoid;
    // URL正则匹配:
    Pattern urlPattern;
    // Bean实例:
    Object controller;
    // 处理方法:
    Method handlerMethod;
    // 方法参数:
    Param[] methodParameters;
}

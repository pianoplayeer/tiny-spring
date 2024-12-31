package org.jxy.spring.web;

import java.io.BufferedReader;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jxy.spring.exception.HttpParamLackException;
import org.jxy.spring.exception.ServerErrorException;
import org.jxy.spring.utils.JsonUtils;
import org.jxy.spring.utils.WebUtils;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

    public boolean matches(String url) {
        return urlPattern.matcher(url).matches();
    }

    public Result handle(String url, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object[] arguments = new Object[methodParameters.length];
        Matcher matcher = urlPattern.matcher(url);

        for (int i = 0; i < methodParameters.length; i++) {
            Param param = methodParameters[i];
            arguments[i] = switch (param.paramType) {
                case PATH_VARIABLE -> {
                    try {
                        String arg = matcher.group(param.name);
                        yield convertToType(param.classType, arg);
                    } catch (IllegalArgumentException e) {
                        throw new HttpParamLackException("Path parameter " + param.name + " not found.");
                    }
                }

                case REQUEST_PARAM -> {
                    String arg = getOrDefault(request, param.name, param.defaultValue);
                    yield convertToType(param.classType, arg);
                }

                case REQUEST_BODY -> {
                    BufferedReader reader = request.getReader();
                    yield JsonUtils.readJson(reader, param.classType);
                }

                case SERVLET_VARIABLE -> {
                    Class<?> classType = param.classType;
                    if (classType == HttpServletRequest.class) {
                        yield request;
                    } else if (classType == HttpServletResponse.class) {
                        yield response;
                    } else if (classType == HttpSession.class) {
                        yield request.getSession();
                    } else if (classType == ServletContext.class) {
                        yield request.getServletContext();
                    } else {
                        throw new ServerErrorException("Could not determine argument type: " + classType);
                    }
                }
            };

        }
    }

    private String getOrDefault(HttpServletRequest request, String name, String defaultValue) {
        String arg = request.getParameter(name);
        if (arg == null) {
            if (WebUtils.REQUEST_PARAM_DEFAULT.equals(defaultValue)) {
                throw new HttpParamLackException("Request parameter " + name + " not found.");
            }
            return defaultValue;
        }

        return arg;
    }

    private Object convertToType(Class<?> classType, String arg) {
        if (classType == String.class) {
            return arg;
        } else if (classType == boolean.class || classType == Boolean.class) {
            return Boolean.valueOf(arg);
        } else if (classType == int.class || classType == Integer.class) {
            return Integer.valueOf(arg);
        } else if (classType == long.class || classType == Long.class) {
            return Long.valueOf(arg);
        } else if (classType == byte.class || classType == Byte.class) {
            return Byte.valueOf(arg);
        } else if (classType == short.class || classType == Short.class) {
            return Short.valueOf(arg);
        } else if (classType == float.class || classType == Float.class) {
            return Float.valueOf(arg);
        } else if (classType == double.class || classType == Double.class) {
            return Double.valueOf(arg);
        } else {
            throw new ServerErrorException("Could not determine argument type: " + classType);
        }
    }
}

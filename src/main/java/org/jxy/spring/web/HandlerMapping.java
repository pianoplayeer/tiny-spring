package org.jxy.spring.web;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

public class HandlerMapping {
    private List<Handler> handlers = new ArrayList<>();

    public Handler getHandler(HttpServletRequest request) {

    }
}

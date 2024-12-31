package org.jxy.spring.web;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class HandlerInterceptor {
    private Interceptor interceptor;

    private List<Pattern> pathPatterns = new ArrayList<>();

    private List<Pattern> excludePathPatterns = new ArrayList<>();
}

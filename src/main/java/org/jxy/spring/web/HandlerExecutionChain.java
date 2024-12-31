package org.jxy.spring.web;

import java.util.ArrayList;
import java.util.List;

public class HandlerExecutionChain {
    private Handler handler;

    private List<HandlerInterceptor> interceptorChain = new ArrayList<>();

    public Result process() {

    }


}

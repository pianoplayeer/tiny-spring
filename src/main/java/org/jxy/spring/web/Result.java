package org.jxy.spring.web;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Result {
    private boolean processed;

    private Object returnObject;
}

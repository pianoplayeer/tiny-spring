package org.jxy.spring.utils;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamCallback<T> {
    T doWithInputStream(InputStream stream) throws IOException;
}

package org.jxy.spring.aop.metric;


import org.jxy.spring.annotation.Aspect;
import org.jxy.spring.annotation.Component;

@Component
@Aspect("metricInvocationHandler")
public class HashWorker extends BaseWorker {

    @Metric("SHA-1")
    public final String sha1(String input) {
        return hash("SHA-1", input);
    }

    @Metric("SHA-256")
    public String sha256(String input) {
        return hash("SHA-256", input);
    }
}

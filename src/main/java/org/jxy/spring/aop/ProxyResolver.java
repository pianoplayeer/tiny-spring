package org.jxy.spring.aop;

import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @date 2024/11/2
 * @package org.jxy.spring.aop
 */
@Slf4j
public class ProxyResolver {
	private static ByteBuddy byteBuddy = new ByteBuddy();
	
	public static <T> T createProxy(T bean, InvocationHandler handler) {
		Class<?> targetClass = bean.getClass();
		log.debug("create proxy for bean {}", targetClass.getName());
		Class<?> proxyClass = byteBuddy.subclass(targetClass, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR)
				.method(ElementMatchers.isPublic())
				.intercept(InvocationHandlerAdapter.of(new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						return handler.invoke(bean, method, args);
					}
				}))
				.make()
				.load(targetClass.getClassLoader())
				.getLoaded();

		Object proxy;
		try {
			proxy = proxyClass.getConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return (T) proxy;
	}
}

package org.jxy.spring.boot;

import lombok.AllArgsConstructor;

/**
 * @date 2025/2/13
 * @package org.jxy.spring.boot
 */
public class Server {
	// tomcat/jetty
	private Object delegate;
	
	public Server(ServerKind kind, int port, )
	
	public void start() throws Exception {
		if (delegate instanceof org.apache.catalina.Server tomcat) {
			tomcat.start();
		} else if (delegate instanceof org.mortbay.jetty.Server jetty) {
			jetty.start();
		}
	}
	
	public void await() throws InterruptedException {
		if (delegate instanceof org.apache.catalina.Server tomcat) {
			tomcat.await();
		} else if (delegate instanceof org.mortbay.jetty.Server jetty) {
			jetty.join();
		}
	}
}

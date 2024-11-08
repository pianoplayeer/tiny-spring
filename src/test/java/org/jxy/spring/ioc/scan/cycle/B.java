package org.jxy.spring.ioc.scan.cycle;

import org.jxy.spring.annotation.Autowired;
import org.jxy.spring.annotation.Component;

/**
 * @date 2024/11/2
 * @package org.jxy.spring.ioc.scan.cycle
 */
@Component
public class B {
	@Autowired
	private A a;
}

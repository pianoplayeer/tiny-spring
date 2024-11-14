package org.jxy.spring.jdbc.transaction;

import org.jxy.spring.annotation.Transactional;
import org.jxy.spring.aop.processor.AutoProxyCreator;

public class TransactionalBeanPostProcessor extends AutoProxyCreator<Transactional> {
}

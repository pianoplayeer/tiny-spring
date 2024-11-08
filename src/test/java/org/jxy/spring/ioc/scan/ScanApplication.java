package org.jxy.spring.ioc.scan;


import org.jxy.spring.annotation.ComponentScan;
import org.jxy.spring.annotation.Import;
import org.jxy.spring.ioc.imported.LocalDateConfiguration;
import org.jxy.spring.ioc.imported.ZonedDateConfiguration;

@ComponentScan
@Import({ LocalDateConfiguration.class, ZonedDateConfiguration.class })
public class ScanApplication {

}

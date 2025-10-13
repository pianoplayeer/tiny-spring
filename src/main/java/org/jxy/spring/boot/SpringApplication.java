package org.jxy.spring.boot;

import lombok.extern.slf4j.Slf4j;
import org.jxy.spring.utils.FileUtils;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

@Slf4j
public class SpringApplication {
    
    private Class<?> configClass;
    
    public static void run(Class<?> primarySource, String... args) {
        new SpringApplication(primarySource).run(args);
    }
    
    public SpringApplication(Class<?> configClass) {
        this.configClass = configClass;
    }
    
    private void run(String... args) {
        printBanner();
    
        long startTime = System.currentTimeMillis();
        int javaVersion = Runtime.version().feature();
        long pid = ManagementFactory.getRuntimeMXBean().getPid();
        String user = System.getProperty("user.name");
        String pwd = Paths.get("").toAbsolutePath().toString();
        log.info("Starting {} using Java {} with PID {} (started by {} in {})", configClass.getSimpleName(), javaVersion, pid, user, pwd);
        
        
        
        long endTime = System.currentTimeMillis();
        String appTime = String.format("%.3f", (endTime - startTime) / 1000.0);
        String jvmTime = String.format("%.3f", ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0);
        log.info("Started {} in {} seconds (process running for {})", configClass.getSimpleName(), appTime, jvmTime);
    
        
    }
    
    private void printBanner() {
        String bannerPath = "/banner.txt";
        String banner = FileUtils.readFile(bannerPath, StandardCharsets.UTF_8);
        banner.lines().forEach(System.out::println);
    }
}

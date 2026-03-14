package com.revworkforce.leaveperformance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.revworkforce.leaveperformance", "com.revworkforce.common"})
public class LeavePerformanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeavePerformanceApplication.class, args);
    }
}

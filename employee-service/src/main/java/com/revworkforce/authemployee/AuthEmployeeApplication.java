package com.revworkforce.authemployee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.revworkforce.authemployee", "com.revworkforce.common"})
public class AuthEmployeeApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthEmployeeApplication.class, args);
    }
}

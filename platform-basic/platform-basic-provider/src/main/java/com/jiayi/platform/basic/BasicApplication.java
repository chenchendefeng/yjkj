package com.jiayi.platform.basic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author : weichengke
 * @date : 2019-02-28 09:01
 */
@SpringBootApplication
@EnableEurekaClient
@EnableAspectJAutoProxy
@ComponentScan("com.jiayi")
public class BasicApplication {

    public static void main(String[] args) {
        SpringApplication.run(BasicApplication.class, args);
    }
}

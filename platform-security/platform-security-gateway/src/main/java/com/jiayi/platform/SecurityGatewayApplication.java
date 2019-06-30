package com.jiayi.platform;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

@EnableEurekaClient
@SpringBootApplication
@Slf4j
public class SecurityGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes().route(t -> t.path("/**").and().readBody(Object.class, requestBody -> {
            log.info("requestBody is {}", requestBody);
            return false;
        }).uri("http://127.0.0.1:10005")).build();
    }
}

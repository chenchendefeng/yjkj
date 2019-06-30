package com.jiayi.platform.judge;

import com.jiayi.platform.judge.manage.ScheduleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author : weichengke
 * @date : 2019-04-18 16:47
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan("com.jiayi")
@EnableScheduling
@EnableEurekaClient
@EnableFeignClients(basePackages="com.jiayi")
public class JudgeApplication implements ApplicationRunner {
    @Autowired
    private ScheduleManager scheduleManager;


    public static void main(String[] args) {
        SpringApplication.run(JudgeApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        scheduleManager.reCalculateQueries();
    }
}

package com.fishman.oj_backend_judge_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@ComponentScan("com.fishman")
public class OjBackendJudgeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjBackendJudgeServiceApplication.class, args);
    }

}

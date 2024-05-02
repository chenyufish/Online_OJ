package com.fishman.oj_backend_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OjBackendGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjBackendGatewayApplication.class, args);
    }

}

package com.wq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

@SpringBootApplication
@EnableHystrixDashboard // 开启Hystrix监控页面
public class DeptConsumer_hystrix80 {
    public static void main(String[] args) {
        SpringApplication.run(DeptConsumer_hystrix80.class, args);
    }
}

package com.wq;

import com.MyIRule.FeignWQRule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableEurekaClient // 消费者也是Eureka服务注册中心(服务器)的客户
@EnableFeignClients(basePackageClasses = {com.wq.service.DeptFeignClientService.class})
@RibbonClient(name="SpringCloud-provider-dept",configuration = FeignWQRule.class)
//@ComponentScan("com.wq")
public class DeptConsumer_feign80 {
    public static void main(String[] args) {
        SpringApplication.run(DeptConsumer_feign80.class, args);
    }

}

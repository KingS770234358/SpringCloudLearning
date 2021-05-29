package com.wq;

import com.netflix.hystrix.HystrixMetrics;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

/**
 * 主启动类
 */
@SpringBootApplication
@EnableEurekaClient // 在服务启动后 自动注册到 EurekaServer 服务注册中心中
@EnableDiscoveryClient // 服务发现 用于获取注册中心里面的服务信息
@EnableCircuitBreaker //添加对熔断机制的支持 CircuitBreaker 断路器
public class DeptProvider_8001 {
    public static void main(String[] args) {
        SpringApplication.run(DeptProvider_8001.class, args);
    }
    // 增加一个Servlet用于 Hystrix监控页面的访问
    @Bean // 注入到Spring容器中
    public ServletRegistrationBean hystrixServlet(){
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new HystrixMetricsStreamServlet());
        // url映射
        //servletRegistrationBean.addUrlMappings("/actuator/hystrix.stream");
        servletRegistrationBean.setLoadOnStartup(1);
        servletRegistrationBean.addUrlMappings("/actuator/hystrix.stream");
        servletRegistrationBean.setName("HystrixMetricsStreamServlet");
        System.out.println("hello-----");
        return servletRegistrationBean;
    }
}

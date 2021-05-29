package com.wq.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ConfigBean { // @Configuration就相当于 原来的Spring 的 applicationContext.xml

    // 配置负载均衡的RestTemplate
    @Bean(name="LB_RT")
    @LoadBalanced // Ribbon基于客户端实现负载均衡
    public RestTemplate getLBRestTemplate(){
        return new RestTemplate();
    }


    // 将一个RestTemplate bean注入Spring容器中
    @Bean(name="common_RT")
    public RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}

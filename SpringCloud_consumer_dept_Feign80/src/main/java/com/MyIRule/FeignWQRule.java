package com.MyIRule;

import com.netflix.loadbalancer.IRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignWQRule {



    @Bean
    public IRule myRule(){
        // 默认是轮询
        //return new RandomRule();
        // 注入自己写的负载均衡算法
        return new FiveTimeRule();
    }

}

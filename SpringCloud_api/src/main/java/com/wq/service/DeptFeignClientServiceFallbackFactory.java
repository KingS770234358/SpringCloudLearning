package com.wq.service;

import com.wq.pojo.Dept;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeptFeignClientServiceFallbackFactory implements FallbackFactory {
    @Override
    public DeptFeignClientService create(Throwable throwable) {
        /***
         * 服务降级
         * 降级之后的服务
         * 存在于消费者这边
         */
        return new DeptFeignClientService(){

            @Override
            public List<Dept> feignGetAllDept() {
                return null;
            }

            @Override
            public Dept feignGetDept(Long deptId) {
                return new Dept()
                        .setDeptno(deptId)
                        .setDname("未知")
                        .setDbSource("未知---服务提供者已经关闭,这是消费者提供的服务降级信息");
            }

            @Override
            public boolean feignAddDept(Dept dept) {
                return false;
            }
        };
    }
}

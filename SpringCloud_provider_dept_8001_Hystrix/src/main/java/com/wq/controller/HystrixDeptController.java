package com.wq.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.ribbon.proxy.annotation.Hystrix;
import com.wq.pojo.Dept;
import com.wq.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/***
 * 测试Hystrix的控制器
 */
// 提供RestFul服务!
@RestController
public class HystrixDeptController {

    @Autowired
    private DeptService deptService;


    @GetMapping("/dept/getDept/{id}")
    @HystrixCommand(fallbackMethod = "hystrixGet")
    public Dept getDeptByIDHystrix(@PathVariable("id") Long deptId){
        Dept dept = deptService.queryById(deptId);
        // 通过异常捕获的方式处理
        if(dept==null){
            throw new RuntimeException("id:"+deptId+"- 不存在或者信息无法找到!");
        }
        return dept;
    }

    // 备选方案 (熔断)
    public Dept hystrixGet(@PathVariable("id") Long deptId){
        return new Dept()
                .setDeptno(deptId)
                .setDname("未知")
                .setDbSource("未知---@Hystrix");
    }

}

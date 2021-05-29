package com.wq.controller;

import com.wq.pojo.Dept;
import com.wq.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 提供RestFul服务!
@RestController
public class DeptController {

    @Autowired
    private DeptService deptService;

    // 添加一个用户
    @PostMapping("/dept/addDept")
    public boolean addDept(@RequestBody Dept dept){
        System.out.println(dept);
        return deptService.addDept(dept);
    }

    // 添加一个用户
    @GetMapping("/dept/getDept/{id}")
    public Dept getDept(@PathVariable("id")Long deptId){
        return deptService.queryById(deptId);
    }
    // 添加一个用户
    @GetMapping("/dept/getAllDept")
    public List<Dept> getAllDept(){
        return deptService.queryAllDept();
    }

    //========================eureka==================================================
    @Autowired //获得一些配置的信息,得到具体的微服务
    private DiscoveryClient client;  // 是SpringCloud的 不是Netflix的
    // 通过EurekaDiscoveryClient获取服务注册中心和SpringCloud的一些消息(团队开发需要)
    @GetMapping("/dept/discovery")
    public Object discovery(){
        // 获得微服务列表的清单
        List<String> services = client.getServices();
        System.out.println("discovery---services:"+services);
        // 得到一个具体的应用下面所有的微服务信息 applicationName
        List<ServiceInstance> instances =  client.getInstances("SPRINGCLOUD_PROVIDER_DEPT_8001");
        for (ServiceInstance si : instances){
            System.out.println(
                    si.getHost()+"\t"+
                    si.getPort()+"\t"+
                    si.getUri()+"\t"+
                    si.getServiceId()
            );
        }
        return this.client;
    }

}

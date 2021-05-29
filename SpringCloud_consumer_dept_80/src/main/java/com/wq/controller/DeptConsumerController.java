package com.wq.controller;

import com.wq.pojo.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DeptConsumerController {
    // 理解:消费者不应该有service层

    // RestFul   RestTemplate 供我们直接调用 要使用要先注册到Spring中
    // 要在Config中配置 返回一个RestTemplate 用@Bean的方式注入Spring中
    @Autowired
    @Qualifier("common_RT")
    // (url, 实体:Map, class<T> responseType) 参数1:url 参数2:请求(包含的实体) 参数3:返回值类型
    private RestTemplate restTemplate; // 提供多种便捷访问远程http服务的方法 是一种简单的restful风格服务模板

    // url前缀常量
    private static final String REST_URL_PREFIX = "http://localhost:8003";

    // 通过id获得某个用户
    // /dept/getDept/1
    @RequestMapping("/consumer/dept/getDpetById/{id}")
    public Dept dept(@PathVariable("id")Long id){
        // 这里getForObject的get就是get提交方式=====要和隔壁服务器的Mapping方式对应上
        // Object通用 想要获得什么都行
        return restTemplate.getForObject(REST_URL_PREFIX+"/dept/getDept/"+id,Dept.class);
    }

    // 查看所有部门
    @RequestMapping("/consumer/dept/getAllDept")
    public List<Dept> getAllDept(){
        return restTemplate.getForObject(REST_URL_PREFIX+"/dept/getAllDept",List.class);
    }

    // 添加部门
    @RequestMapping("/consumer/dept/addDept")
    public Boolean addDept(Dept dept){
        System.out.println(dept);
        return restTemplate.postForObject(REST_URL_PREFIX+"/dept/addDept",dept,Boolean.class);
    }
}

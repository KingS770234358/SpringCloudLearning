package com.wq.controller;

import com.wq.pojo.Dept;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/***
 * 测试Ribbon负载均衡的控制器
 */
@RestController
public class LBDeptConsumerController {

    @Autowired
    @Qualifier("LB_RT")
    private RestTemplate LB_restTemplate;

    // url前缀常量
    private static final String LB_REST_URL_PREFIX = "http://SpringCloud-provider-dept";

    // 查看所有部门
    @RequestMapping("/consumer/dept/getAllDeptLB")
    public List<Dept> getAllDept(){
        return LB_restTemplate.getForObject(LB_REST_URL_PREFIX+"/dept/getAllDept",List.class);
    }
    @RequestMapping("/consumer/dept/getDpetByIdLB/{id}")
    public Dept dept(@PathVariable("id")Long id){
        // 这里getForObject的get就是get提交方式=====要和隔壁服务器的Mapping方式对应上
        // Object通用 想要获得什么都行
        return LB_restTemplate.getForObject(LB_REST_URL_PREFIX+"/dept/getDept/"+id,Dept.class);
    }
}

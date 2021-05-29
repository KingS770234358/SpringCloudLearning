package com.wq.controller;

import com.wq.pojo.Dept;
import com.wq.service.DeptFeignClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/***
 * 测试Feign的控制器
 */
@RestController
public class FeignLBDeptConsumerController {
    // 注入FeignClient接口(封装了Ribbon和RestTemplate)
    @Autowired
    DeptFeignClientService deptFCS;
    // 查看所有部门
    @RequestMapping("/consumer/dept/getAllDeptLBFeign")
    public List<Dept> getAllDept(){
        return deptFCS.feignGetAllDept();
    }
    @RequestMapping("/consumer/dept/getDeptByIdLBFeign/{id}")
    public Dept dept(@PathVariable("id")Long id){
        // 这里getForObject的get就是get提交方式=====要和隔壁服务器的Mapping方式对应上
        // Object通用 想要获得什么都行
        return deptFCS.feignGetDept(id);
    }
}

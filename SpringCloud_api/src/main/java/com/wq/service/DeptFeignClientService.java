package com.wq.service;

import com.wq.pojo.Dept;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/***
 * Feign封装RestTemplate用的接口
 */
//// 视频里使用@Component注解将这个FeignClient接口注入到容器中
    // 但是这个版本 @FeignClient注解就直接将它注入到了容器中
//@Component
// 将这个接口声明为FeignClient即Feign的客户端 value指定要去服务注册中心请求的服务名
                                               // 绑定降级服务 fallbackFactory
@FeignClient(value = "SpringCloud-provider-dept",fallbackFactory = DeptFeignClientServiceFallbackFactory.class)
public interface DeptFeignClientService {

    // 这里的url映射要跟服务提供者的方法的url映射一直 提交方式(Get Post之类)也要一直
    // 用来向服务注册中心发起请求
    @GetMapping("/dept/getAllDept")
    public List<Dept> feignGetAllDept();

    // 添加一个用户
    @GetMapping("/dept/getDept/{id}")
    public Dept feignGetDept(@PathVariable("id")Long deptId);

    // 添加一个用户
    @PostMapping("/dept/addDept")
    public boolean feignAddDept(@RequestBody Dept dept);
}

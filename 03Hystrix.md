03Hystrix 容灾机制 服务熔断机制 服务降级

引入
· 扇出:多个微服务之间先后调用,微服务A调用微服务B和微服务C,微服务B和微服务C调用其他微服务
  称为扇出
· 当扇出的链路上某个服务响应时间过长或不可用,对微服务A的调用就会占用越来越多的系统资源(因为有越来越多的用户访问)
  进而引起系统崩溃,就是所谓的"雪崩效应"
· 单个后端的依赖关系雪崩可能导致整个系统发生更多的级联故障, 因此需要对故障和延迟进行隔离和管理, 
  以便单个依赖关系的失败, 不会取消整个应用程序或系统
  ===>弃车保帅

Hystrix 
· github官网:github.com/Netflix/Hystrix/wiki
· Hystrix: 是一个用于处理分布式系统的延迟和容错的开源库, 保证单个依赖的调用出问题的时候不会影响整体服务,
  避免级联故障, 提高分布式系统的弹性
· "断路器"本是一种开关装置, 当某个服务单元发生故障后, 通过断路器的故障监控(类似熔断保险丝),
  向调用方返回一个[服务预期的 可处理的备选响应(fallback)], 而不是长时间的等待或抛出调用方法无法处理的异常
  这样保证了服务调用方法的时间不会被长时间、不必要的占用, 避免了故障在分布式系统中的蔓延甚至雪崩
· 作用: 服务降级、服务熔断、服务限流、接近实时的监控...
· 自己可以使用 异常、异步加载实现这种熔断机制

服务熔断
· 应对雪崩效应的一种服务链路保护机制
· 当扇出链路的某个微服务不可用或响应时间太长的时候, 会进行服务降级, 进而熔断该节点微服务的调用, 快速返回
  错误的响应信息 当检测到该节点微服务调用响应正常恢复调用链路
· Hystrix会监控服务之间调用的情况, 当失败调用到一定的阈值, 缺省是5s内20次调用失败就会启动熔断机制
  熔断机制的注解是@HystrixCommand
  
服务熔断实现[在服务提供者端进行熔断]
· 写一个带有熔断机制的服务提供者 Hystrix8001
· 服务提供者pom.xml引入需要的Hystrix启动器依赖
#   @Accessors(chain=true) // 使这个类的set方法支持链式写法
· 编写带有Hystrix熔断的控制器
@GetMapping("/dept/getDeptByIDHystrix/{id}")
[@HystrixCommand(fallbackMethod = "hystrixGet")]
public Dept getDeptByIDHystrix(@PathVariable("id") Long deptId){
    Dept dept = deptService.queryById(deptId);
    // 通过异常捕获的方式处理
    if(dept==null){
        throw new RuntimeException("id:"+deptId+"- 不存在或者信息无法找到!");
    }
    return dept;
}
[备选方案 (熔断)]
public Dept hystrixGet(@PathVariable("id") Long deptId){
    return new Dept()
            .setDeptno(deptId)
            .setDname("未知")
            .setDbSource("未知---@Hystrix");
}
@Hystrix可以理解为只要调用该方法的时候抛出异常,就算失败,就会调用fallbackMethod
[不需要捕获异常即可处理异常 熔断机制]
· 服务提供者主启动类上添加开启@Hystrix的注解
#   服务提供方向注册中心注册时会经常报错 Connection refused: connect 是正常的 多试几次即可
· 测试 启动Eureka服务注册中心集群 启动服务提供者8001Hystrix 启动服务消费者80
  ### 报错:设置测试Hystrix控制器的时候随意定义了方法的路由映射,而消费者请求的路由没改,
  #        为了方便 把Hystrix控制器的方法的路由映射改成对应的
  #   =====>重启服务注册中心 重启服务提供者 重启消费者=====>试几次就好了。。。
#   服务提供者全局配置文件中 加入 
#     eureka:
#        client:
#          service-url:
#            defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
#        instance:
#          instance-id: SpringCloud_provider_dept_8001_hystrix # 修改eureka上的默认描述信息
#          prefer-ip-address: true # 鼠标悬停在Eureka监控页面上Status里的连接上 左下角可以显示真实IP地址

服务降级实现[在服务消费者端进行降级====
这里使用 
消费者:Feign80(以及相关的SpringCloud_api子项目) 
服务提:供者没有实现服务降级的SpringCloud_provider_dept_8001]
· 这个hystrix 服务降级的实现是基于Feign的 所以pom.xml不需要导入新的依赖
· 1.在SpringCloud_api子项目service里编写服务降级FallbackFactory的实现类DeptFeignClientServiceFallbackFactory
  在这里里面创建服务降级之后的服务[参考DeptFeignClientServiceFallbackFactory]
  (代替原来服务提供者里面的整个控制器类或者基于Feign的接口DeptFeignClientService)
  写完这个实现类之后一定不能忘了[使用@Component注解将它注入到Spring容器中]
############################################################################################
  调用过程: 主启动类-->feignController-->FeignClient接口-->注册中心-×->服务提供者提供的方法
                    feignController<--FeignClient接口的<--注册中心-×        
                                FallbackFactory返回的接口实现类
############################################################################################
· 2.在原来的FeignClient接口里用@FeignClient注解绑定fallbackFactory类
· 3.在这个消费者子项目的全局配置文件application.yaml中开启feign的服务降级
· 4.测试:先正常访问 然后关闭服务提供者8001, 再次访问, 即可访问到降级的服务
    ### 这里访问一定要走Feign控制器(里面注入了FeignClient接口)

##############################
# 总结(对比如熔断和服务降级)
##############################
服务熔断: 服务提供者---某个服务超时或者异常就会引起服务熔断

服务降级: 服务消费者---从网站整体的负荷考虑,当某个服务熔断或者关闭之后,该服务将不再被调用,
          此时准备一个该服务的失败回调FallbackFactory返回一个默认值(缺省值)
          好处:整体负载确实变低 
          坏处:整体服务水平降低===>但是保证了可用性!!!Eureka保证了服务的Availability可用性
          
Hystrix监控面板的配置
· 编写一个[服务消费者] SpringCloud_consumer_dept_Hystrix80
  [这个消费者是专门负责Hystrix的监控的 不负责处理请求 因此测试的时候还需要开启之前的消费者应用]
· 导入服务消费者基本的依赖 以及 [Hystrix依赖 和 Hystrix控制面板依赖]
· 编写全局配置文件信息 
  [端口号 9001]
· 编写启动类
  @EnableHystrixDashboard // 开启Hystrix监控页面
================================================================
· 服务提供端也需要一些依赖[所有需要监控的服务pom.xml中都需要有如下依赖]
<!-- hystrix启动器依赖 -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-hystrix</artifactId>
    <version>1.4.7.RELEASE</version>
</dependency>
<!-- actuator完善eureka监控信息的依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
· 测试链接: 消费者控制台报错 Cannot execute request on any known server 不影响
  浏览器访问 localhost:80/hystrix 80是这个消费者服务的端口 进入豪猪页面
· 1)在要监控的服务提供者主启动类里增加一个Servlet bean
  [这里选择SpringCloud_provider_dept_8001]
  
  2)[主启动类上也要使用@EnableCircuitBreaker //添加对熔断机制的支持 CircuitBreaker 断路器]

  3)服务提供者的Controller类里面的接口(就是路由对应的方法上)
    [一定要使用@HystrixCommand注解 用来标识dashboard有监控这个方法,
    如果一个标记@HystrixCommand注解的方法都没有 则面板为空]
· 测试链接2: 
  1)[同时要开启 消费者80 当前这个消费者只是为了监控Hystrix]
  使用消费者80访问服务提供者的服务,注意这里要用LB的方式(其实这个测试中也可以不消费者80 直接访问服务提供者的服务)
  2)访问[要监控的服务提供者]的hystrix监控面板: 服务提供者8001
    先访问http://localhost:8001/actuator/hystrix.stream
    如果这时候服务提供者8001里面[标注了@HystrixCommand的方法]有被调用,则能ping到数据
    再访问Hystrix监控面板消费者9001 localhost:9001/hystrix
    监控http://localhost:8001/actuator/hystrix.stream 监控间隔2s 
    点击监控即可看到面板
### =====> 想要被Hystrix dashBoard监控的类必须
###        1.主启动类开启了@EnableCircuitBreaker,
###          注入了new ServletRegistrationBean(new HystrixMetricsStreamServlet());
###          这个Servlet
###        2.控制器里的接口方法有用注解@HystrixCommand标注的接口方法
###        ===>如果没有第二点,则ping不到数据,监控面板上也不显示东西
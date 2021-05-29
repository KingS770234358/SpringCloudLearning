04Zuul路由网关 -- 结合SpringCloud框架图理解
概述
1.什么是Zuul
Zuul包含了对请求的[路由(跳转) 和 过滤]
·路由负责将外部的请求转发到具体的微服务实例上(消费者上、服务提供者上),
 是实现外部访问统一路口(统一访问地址)的基础
·过滤功能 干预请求的处理过程 是实现请求校验 服务聚合等功能的基础
Zuul和Eureka进行整合,[将Zuul自身注册为Eureka服务治理下的应用];
同时Zuul可以从Eureka服务注册中心中获得其他服务的信息;
以后访问的微服务都是通过
· 总共提供 代理 路由 过滤 三大功能
· 官网:https://github.com/Netflix/zuul

2.创建子项目 SpringCloud_zuul_9527  服务提供者选用8001_Hystrix
· pom.xml中导入一些依赖[必要的+zuul启动器依赖]
· 配置全局配置文件====>[注册到Eureka里面]
· 给Zuul也配一个自己的主机名
  C盘windows system32 driver etc host里最后一行添加
  127.0.0.1  www.wqSpringCloud.com
· 主启动类
  @EnableZuulProxy // 开启Zuul路由网关功能
· 初步测试:开启一个eureka服务注册中心7001 开启一个服务提供者 hystrix_8001
          开启Zuul路由网关
   --- 查看eureka注册中心:http://eureka7001.com:7001/
       里面有一个服务和zuul路由网关         
   [通过 www.wqSpringCloud.com直接访问服务提供者的方法]
   http://www.wqspringcloud.com:9527//springcloud-provider-dept/dept/getDept/1
   ### 1.这里注意 原来8001_Hystrix全局配置文件中 应用名存在大写 为SpringCloud-provider-dept
   ### zuul将其转换成全小写 springcloud-provider-dept
· 2.要使得springcloud-provider-dept变成自己想让用户看到的路径要在zuul子项目的全局配置文件里配置
  ###配置的时候要使用上面[全小写的应用名]
  ## Zuul配置 
  zuul:
    routes:
      applicationName.serviceId: springcloud-provider-dept
      applicationName.path: /customName/**
  ====>可以使用http://www.wqspringcloud.com:9527//customName/dept/getDept/1完成第1点中的访问
· 3.[但是这样还是可以使用http://www.wqspringcloud.com:9527//springcloud-provider-dept/dept/getDept/1
     方式进行访问 没有隐藏真实服务名 springcloud-provider-dept]
  要在Zuul配置中隐藏原项目名
  ## Zuul配置
  zuul:
    routes:
      applicationName.serviceId: springcloud-provider-dept
      applicationName.path: /customName/**
    ignored-services: springcloud-provider-dept
  4.一般要使用通配符隐藏所有的真实服务名  然后加上公共的前缀
  ## Zuul配置  [隐藏所有的真实服务名  加上公共的前缀]
  zuul:
    routes:
      applicationName.serviceId: springcloud-provider-dept
      applicationName.path: /customName/**
    ignored-services: "*"
    prefix: /wq
  =====效果:原来http://www.wqspringcloud.com:9527//customName/dept/getDept/1可以访问
            现在http://www.wqspringcloud.com:9527/wq/customName/dept/getDept/1才能访问
################## Zuul就相当于之前实验的消费者 控制器的角色 ###########################################
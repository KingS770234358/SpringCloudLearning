### SpringCloud引言
1. 回顾之前的知识
· javaSE
· 数据库
· 前端
· Servlet的配置
· Http
· Mybatis
· Spring
· SpringMVC
· SpringBoot
· Dubbo Zookeeper 分布式基础
· maven git常规操作
· Ajax json
· .../
2. 串一下自己会的东西
## 以下是必须会的 
· 数据库
· Mybatis
· Spring
· SpringMVC
· SpringBoot
· Dubbo Zookeeper 分布式基础
· maven git常规操作
· Ajax json
· .../
3. 这个阶段该如何学
· 学什么
三层架构 + MVC
框架
SPring IOC AOP 简化开发

SpringBoot(重构) 新一代的javaEE开发标准 自动装配(约定大于配置)

模块化 all in one 

模块化的开发===== all in one 代码没变化

微服务架构4个核心问题?                          netflix
· 客户端该怎么访问这么多服务    api网关 服务路由 zuul组件
· 服务之间如何通讯             http RPC 通讯 Feign组件                     Dubbo 
· 服务的治理                   服务的注册与发现 Eureka组件                  ZooKeeper
· 服务挂了怎么办               熔断机制 容灾机制 Hystrix

解决方案:
SpringCloud 他就是一个生态 必须掌握SpringBoot

### 常见面试题:
1. 什么是微服务?
2. 微服务的优缺点分别是什么?说说你在项目中遇到的坑
3. 微服务之间是如何独立通讯的
4. SpringCloud和Dubbo有哪些区别
5. 谈谈对SpringBoot和SpringCloud的理解
6. 什么是服务熔断, 什么是服务降级
7. 微服务技术栈有哪些,列举一二
8. eureka和ZooKeeper都可以提供服务注册与发现的功能,有什么区别

4. 微服务架构概述[主要参考第2个视频]
Martin Fowler提出 架构模式 风格
轻量级的通信机制-->http
微服务架构 和 微服务 区别 微服务只是微服务架构里小小的一个组件(Module)

5.什么是SpringCloud
### SpringCloud和SpringBoot的关系
· SpringBoot专注于快速方便的开发单个个体微服务 -jar包
· SpringCloud是关注全局的微服务协调整理治理框架,将SpringBoot开发的一个个单体微服务整合并管理
  起来,为各个微服务之间提供:配置管理,服务发现,断路由,路由,微代理,事件总线,全局锁,决策竞选,
  分布式会话等等集成服务
· SpringBoot可以离开SpringCloud的独立使用,开发项目;SpringCloud离不开SpringBoot,属于依赖关系
总结:SpringBoot专注于快速、方便的开发单个个体微服务, SpringCloud是关注全局的微服务治理框架

6.Dubbo和SpringCloud技术选型
· 分布式+服务治理Dubbo
  目前成熟的互联网架构:应用服务化拆分+消息中间件
· Dubbo和SpringCloud对比

7.SpringCloud
SpringCloud是一个由众多独立子项目组成的大型综合项目,每个子项目有不同的发行节奏,都维护这自己的发布版本号
SpringCloud通过一个资源清单(Bill of Material)来管理每个版本的子项目清单,为了避免与子项目的发布号混淆,
所以没有采用版本号的方式,而是通过命名的方式
命名采用伦敦地铁站的名称,同时根据字母表的顺序对应到版本的时间顺序,
Angel Brixton Canden Dalston Edgware 目前最新的是 Hoxton SR1

### 8.重要参考材料
· 文档:springcloud.cc/spring-cloud-netflix.html
· 中文api文档:springcloud.cc/spring-cloud-dalston.html
· SpringCloud中文社区:springcloud.cn
· SpringCloud中文网:springcloud.cc





































































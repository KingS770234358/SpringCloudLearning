01 Eureka 服务注册与发现 ===== 对比 ZooKeeper zkServer zlCli
1 什么是Eureka
遵循AP原则,是Netflix的一个核心模块, 是一个基于Rest的服务, 用于定位服务, 以实现云端中间层服务发现和故障转移
Eureka的服务EurekaServer需要自己写 Eureka发出[心跳]
三大角色:
EurekaServer: 提供服务的注册与发现
Service Provider: 服务提供者, 将自身服务注册到Eureka中 使消费者能够找到
Service Consumer: 服务消费者, 从Eureka中获取注册服务列表 从而找到消费服务

2.编写Eureka注册中心 02Eureka_7001 [套路:导入依赖->编写配置文件->开启功能->配置类]
· pom.xml文件中导入eureka[服务端]依赖包
· 编写配置文件application.yaml
· 编写一个主启动类使用[@EnableEurekaServer]
====>监控页面的地址是:http://${eureka.instance.hostname}:${server.port}/

3.向服务注册中心注册服务
· 在服务提供者8001子项目pom.xml中加入eureka[普通包]依赖
· 在8001子项目的全局配置文件中编写关于eureka的配置
· 在主启动类上使用[@EnableEurekaClient]注解
  启动主启动类,可以在Eureka监控页面看到8001提供的服务
Application                     AMIs    Availability Zones	Status[可以在配置文件中使用instance-id设置]
SPRINGCLOUD_PROVIDER_DEPT_8001	n/a (1)	(1)	                UP (1) - DESKTOP-TSE95J3:SpringCloud_provider_dept_8001:8001
· 完善监控信息页面
  a.导入pom.xml依赖
  b.在全局配置文件中配置上面Status里面的连接的跳转页面,
    跳转页面上会显示在全局配置文件里自定义配置的json格式信息
· eureka自我保护机制
[症状]:监控页面爆红:EMERGENCY! EUREKA MAY BE INCORRECTLY CLAIMING INSTANCES ARE UP WHEN THEY'RE NOT. RENEWALS ARE LESSER THAN THRESHOLD AND HENCE THE INSTANCES ARE NOT BEING EXPIRED JUST TO BE SAFE.
[解释]:在eureka server注册中心注册着的服务突然挂了,eureka不会立刻清理, 而是对该微服务的信息进行保存
相当于一种熔断机制:宁可保留所有的微服务,也不盲目注销任何可能健康的服务
[关闭方式]:在eurekaServer端的全局配置文件使用 eureka.server.enable-self-preservation=false(不推荐关闭)
· 服务提供者的控制器中通过EurekaDiscoveryClient获取服务注册中心和SpringCloud的一些消息[团队开发需要]
  接口:DiscoveryClient 下面有很多的实现类 EurekaDiscoveryClient[可以用来获取跟SpringCloud相关的信息]
  类:DiscoveryClient implements EurekaClient
  需要在提供者应用的主启动类上加上[@EnableDiscoveryClient]注解
· 消费者访问还是[使用RestFulTemplate访问链接的形式访问],只要链接url写对了,可以访问任何的接口
4.集群配置
· 先修改本机的host文件新增几个ip名 
C:\Windows\System32\drivers\etc下修改host文件 (要获取完全的读取权限)
127.0.0.1       eureka7001.com
127.0.0.1       eureka7002.com
127.0.0.1       eureka7003.com
· 再创建两个服务注册中心 使用上述的ip地址
· 三个服务注册中心[相互关联] 在他们的application.yaml中配置
    defaultZone: http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/
    defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7003.com:7003/eureka/
    defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/
· [把8001服务提供者应用发布到三个服务注册中心上]
    defaultZone: http://eureka7001.com:7001/eureka/,http://eureka7002.com:7002/eureka/,http://eureka7003.com:7003/eureka/

### 5.与ZooKeeper的区别(重点)
RDBMS(MySql Oracle sqlServer) 关系型数据库的 ACID 原则
NoSql(Redis mongoDB) 非关系型数据库的 CAP 原则
CAP原则:                                      ACID原则:
C consistency 强一致性(数据)                   A atomicity 原子性
A availability 可用性                         C consistency 一致性
P partition tolerance 分区容错性               I isolation 隔离性
                                              D durability 持久性
CAP理论的核心,CAP原则不可能三个都满足, 三进二: CA, CP, AP
· 一个分布式系统不可能同时满足CAP三个原则
  CA 单点集群 满足CA 可扩展性较差
  CP 通常性能不是特别高
  AP 对一致性要求低
[Eureka和ZooKeeper对比]分布式系统必须保证P,因此要在AC之间权衡
· ZooKeeper 保证的是 CP
  获得的数据是最新的
  但是当主节点瘫痪的时候 需要选举新的主节点 整个选举过程 整个服务是不可用的 因此可用性不好
· Eureka 保证的是 AP
  获得的数据可能不是最新的
  各个节点是平等的,用户在连接某个节点注册时连接失败可以自动切换至其他节点,只要还有一台Eureka在就能保证服务的可用性
  Eureka的自我保护机制:如果在15分钟内超过85%的节点都没有正常的心跳,Eureka会认为客户端与注册中心发生网路故障,
  1.不在移除注册列表中移除因长时间没有收到心跳而应该过期的服务
  2.仍接收新的服务的注册,但是不会被同步到其他的节点上(因为它猜测网络出现问题)
  3.当网络稳定时, 当前实例新注册的信息会被同步到其他节点中
====>Eureka可以很好地应对网络故障导致部分节点失去联系的情况,而不会像ZooKeeper一样使整个注册服务瘫痪
[http和RPC的区别]
rpc和http的区别是什么
1.rpc和http的存在重大不同的是：
http请求是使用具有标准语义的通用的接口定向到资源的，这些语义能够被中间组件和提供服务的来源机器进行解释。
结果是使得一个应用支持分层的转换(layers of transformation)和间接层(indirection)，
并且独立于消息的来源，这对于一个Internet规模、多个组织、无法控制的可伸缩性的信息系统来说，是非常有用的。
与之相比较，rpc的机制是根据语言的API(language API)来定义的，而不是根据基于网络的应用来定义的。

2.HTTP和RPC的优缺点
主要来阐述HTTP和RPC的异同，让大家更容易根据自己的实际情况选择更适合的方案。
2.1传输协议
RPC:可以基于TCP协议，也可以基于HTTP协议
HTTP:基于HTTP协议
2.2传输效率
RPC:使用自定义的TCP协议，可以让请求报文体积更小，或者使用HTTP2协议，也可以很好的减少报文的体积，提高传输效率
HTTP:如果是基于HTTP1.1的协议，请求中会包含很多无用的内容，
如果是基于HTTP2.0，那么简单的封装以下是可以作为一个RPC来使用的，这时标准RPC框架更多的是服务治理
2.3性能消耗
RPC:可以基于thrift实现高效的二进制传输
HTTP:大部分是通过json来实现的，字节大小和序列化耗时都比thrift要更消耗性能
2.4负载均衡
RPC：基本都自带了负载均衡策略
HTTP：需要配置Nginx，HAProxy来实现
2.5服务治理
RPC：能做到自动通知，不影响上游
HTTP:需要事先通知，修改Nginx/HAProxy配置
2.6总结
RPC主要用于公司内部的服务调用，性能消耗低，传输效率高，服务治理方便。
HTTP主要用于对外的异构环境，浏览器接口调用，APP接口调用，第三方接口调用等。
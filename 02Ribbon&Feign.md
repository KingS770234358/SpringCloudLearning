02 Ribbon=====负责负载均衡
1.负载均衡算法
· 轮询:每个服务器轮流接收用户的请求
· 随机: 哈希等, 随机将用户的请求分配给某一台服务器

2.Ribbon
· SpringCloud Ribbon是基于Netflix Ribbon实现的一套 [客户端负载均衡的工具]
· 提供客户端软件的负载均衡算法, 将NetFlix的中间层服务(服务集群)连接在一起。
· Ribbon的客户端组件提供:连接超时、重试等等一系列完整的配置
· 简单来说,就是在配置文件中列出LoadBalancer(LB,负载均衡)后面所有的机器, 
  Ribbon会自动帮助你基于某种规则去连接这些机器,很容易就可以使用Ribbon自定义的负载均衡算法

3.Ribbon的作用
· 简单的说就是将用户的请求平摊的多个服务器上,从而达到系统的HA(High Availability)
· 常用的负载均衡软件Nginx Lvs(Linux Virtual Server) Apache+Tomcat 等
· dubbo SpringCloud都提供了负载均衡, SpringCloud可以自定义负载均衡算法
· 负载均衡简单分类
  集中式LB:Nginx(反向代理服务器)所有的请求都集中在Nginx,由Nginx进行分配,该将请求交给哪台服务器
  进程式LB:消费方从服务注册中心获知有哪些地址可用,然后自己从这些地址中选出一个合适的服务器
  [Ribbon就属于进程类LB]它是一个类库,集成于消费方进程, 消费方通过它来获得服务提供方的地址以及服务器的状态
  =====>以下配置主要集中在consumer端

4.消费者应用集成Ribbon
· pom.xml中导入ribbon依赖(SpringCloud的一个启动器)
  因为要在Eureka中寻找服务,所以需要引入Eureka[普通]依赖
· 在全局配置文件中进行相应的配置
  [这里注意对比三种角色的设置 服务提供者 Eureka服务中心 服务消费者
   后两者都不需要向服务中心注册自己]
  这里注意服务的来源有多个(这里3个)
· 在主启动类上也要加上@EnableEurekaClient注解[服务提供者和服务消费者 都是 Eureka服务中心的客户]
· 在config进行负载均衡的配置
  直接在RestTemplate上加上@LoadBalanced注解即可
· [控制器]之前在控制器中写死了去哪个IP的哪个端口获取服务,现在Ribbon进行动态的选择
  因此要通过applicationName服务名(服务提供者应用名)[eureka监控页面上的application一栏]来获取服务
===>启动Eureka注册中心服务集群===>启动服务提供者注册服务===>启动服务消费者进行消费
[浏览器地址栏请求的是消费者应用 80端口 默认可以不写]
### 报错:Request URI does not contain a valid hostname:
### 注意: 服务提供者的应用全局配置文件中配置应用名称的时候 可以使用中划线 但不能使用下划线!
[总结 eureka 和 Ribbon 整合之后]浏览器可以直接访问消费者应用,
消费者应用会根据既定的策略访问服务集群里服务注册中心里面注册的某个服务提供者应用,不关心IP地址和端口号

###4. 三个端口(服务器)提供统一种服务(相同的服务提供者应用) 通过负载均衡决定调用哪个端口(服务器)的服务
Eureka服务注册中心服务集群:1个 7001
服务提供者:3个 8001 8002 8003 分别对应三个不同的数据库 db01 db02 db03
服务消费者:1个 80
· 先再建两个和db01一样 除了datasource字段不一样的数据库
· 再建两个8002 8003服务提供者
  pom.xml文件依赖 yaml配置文件(注意数据库要变) 
  mybatis配置及Mapper.xml 
  com.wq下的所有东西(主启动类需要refactor)
  [~~~~~三个项目都需要有相同的应用名 即在全局配置文件application.yaml中
  spring.application.name: SpringCloud-provider-dept 不能使用下划线!]
### 报错:Invalid bound statement (not found)
#   在项目之间的复制过程中,先复制了DeptMapper.xml文件,然后在复制com.wq包导8003项目下,
#   因为这样复制com这一级会消失,所以又新建了一个com,然后把wq拖了进去,此时refactor导致
#   DeptMapper.xml命名空间处的com.wq....变成了com.com.wq....
##################################################################################
### 报错:发现从部门表里取出的数据源db_source字段一直为空
#   因为mybatis.xml配置文件里开启了将下划线转成驼峰命名 应该使用dbSouce接收
#   而对应的实体类中字段却是db_source
#   因此 要么该所有的mybatis.xml配置文件  要么修改实体类db_source为dbSource
##################################################################################
### 注意访问的时候一定访问的url一定要是带有LB的,LB的才是实现了负载均衡的 
#   否则dbSource这个字段不会变
=====> 实验结果:ribbon默认使用轮询的负载均衡算法

5.自定义负载均衡算法(消费者端===Ribbon是在消费者端的进程式负载均衡)
回到消费者的RestTemplate配置上找到@LoadBalanced注解(ctrl点进去查看 其实就是一个简单的接口)
最主要的是这个[叫做IRule的接口====负载均衡实现的路由网关] 双击shift查看IRule接口(可以找到它下面的实现类)
实现类:
· 抽象的 一般不用 [用于继承]
· AvailabilityFilteringRule:会先过滤崩溃、[跳闸]、访问时存在故障的服务,然后轮询访问
· RoundRobinRule: [默认的无条件轮询算法]===下载源码方便看
· RandomRule [随机]===下载源码方便看
· WeightedResponseTimeRule 加权响应时间规则
· RetryRule: 假设轮询获得服务失败,会在指定的时间内重试 
[使用其他负载均衡算法]
直接在@LoadBalance的设置下面写一个返回IRule对象的方法 再@Bean注入Spring容器即可
    @Bean
    public IRule myRule(){
        return new RandomRule();
    }
### Ribbon使用规范:
# IRule的配置不能写在跟主启动类同级,以及同级的文件夹下;要写在跟主启动类所在的文件夹同级文件夹下
# 比如 com.kuang.springcloud下有controller config 等等 以及 主启动类
# 那么 IRule的配置类可以放在com.kuang.myrule下 KuangRule.java[被@Configuration注解标志]
# 然后在主启动类上
# 使用@RibbonClient(name = 要走负载均衡的[服务名] ~~~~处标志的地方, configuration=KuangRule.class)
# 配置之后会覆盖默认的IRule

警告:KuangRule.java[被@Configuration注解标志],它不在主应用程序上下文的@ComponentScan中,
[否则将会被所有的RibbonClients]共享
如果使用了@ComponentScan或者@SpringBootApplication,则需要采用措施避免包含
比如将其放在单独的、不重叠的包中;或者指定要在@ComponentScan

6写一个算法 访问每个服务5次 然后换下一个服务 循环
·从RandomRule源码中把代码全部赋值出来 直接就可以复制到MyIRule文件夹下形成.java文件
·修改类名
·加上注解 @RibbonClient(name="SpringCloud-provider-dept",configuration = WQRule.class)

7.Feign
· 总体:Feign其实就是封装了Ribbon和RestTemplate;消费者应用通过调用FeignClient类型的接口,
  来获得服务名称以及服务对应的路由 然后再去Eureka的服务注册中心集群里寻找对应的方法进行调用
· 步骤:
  1)在SpringCloud_api下的service编写接口 把RestTemplate形式的url进行封装
  ### 这里因为这个子项目导入了SpringCloud_api子项目,所以可以把这个接口放到SpringCloud_api下编写
  SpringCloud_api要导入feign的启动器依赖!
  /***
   * Feign封装RestTemplate用的接口
   */
  ### 视频里使用@Component注解将这个FeignClient接口注入到容器中
  // 但是这个版本 @FeignClient注解就直接将它注入到了容器中
  // @Component
  // 将这个接口声明为FeignClient即Feign的客户端 value指定要去服务注册中心请求的服务名
  @FeignClient(value = "SpringCloud-provider-dept")
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
  2)写一个基于Feigb的消费者 跟80消费者应用差不多
    · pom.xml中要导入feign启动器依赖
    · 主启动类去掉 @RibbonClient的注解 加上@FeignClient注解 并指定要使用的用于封装RestTemplate的接口
    如(1)所写
    @EnableFeignClients(basePackageClasses = {com.wq.service.DeptFeignClientService.class})
    或者@EnableFeignClients(basePackages={""com.wq.service"})
    ## 提示重复定义--这步视频里做了 但是应该是多余的:同时使用@ComponentScan注解扫描本消费者项目本身的一些组件
    · 编写控制器类
     // 注入FeignClient接口(封装了Ribbon和RestTemplate)
     @Autowired
     DeptFeignClientService deptFCS;
     // 查看所有部门
     @RequestMapping("/consumer/dept/getAllDeptLBFeign")
     public List<Dept> getAllDept(){
         return deptFCS.feignGetAllDept();
     }
     @RequestMapping("/consumer/dept/getDpetByIdLBFeign/{id}")
     public Dept dept(@PathVariable("id")Long id){
         // 这里getForObject的get就是get提交方式=====要和隔壁服务器的Mapping方式对应上
         // Object通用 想要获得什么都行
         return deptFCS.feignGetDept(id);
     }
  3)配置Ribbon负载均衡算法 
    还是在主启动类上@RibbonClient(name="SpringCloud-provider-dept",configuration = FeignWQRule.class)
    pom.xml文件要多导入Ribbon的启动类依赖
  4)测试:主启动类-->feignController-->FeignClient接口-->注册中心-->服务提供者提供的方法
##########################################################################################
#  总结
#  · 调用过程: 主启动类-->feignController-->FeignClient接口-->注册中心-->服务提供者提供的方法
#  · Feign作用就是封装RestTemplate[通过接口方法调用],本质上底层还是调用RestTemplate
#  代码可读性提高----[因为封装性能降低]
#  · 负载均衡的工作还是交给Ribbon完成,可以理解为跟Feign毫无关系,Feign只是很好的兼容了Ribbon
#    暂且这么理解....
##########################################################################################
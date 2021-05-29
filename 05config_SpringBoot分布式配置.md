0505config_SpringBoot分布式配置(Netflix已结束 )
[对应SpringCloud官网框架图中的Config dashboard模块]
概述
·分布式系统面临的配置文件问题
微服务意味着要将单体应用中的业务拆分成一个个自服务,每个服务的粒度相对较小
因此系统中会出现大流量的服务,由于[每个服务都需要必要的配置信息才能运行],
动态的配置管理设施必不可少,SpringCloud提供了[ConfigServer]来解决这个问题
每一个微服务带着一个application.yaml, 上百个这种配置文件将难以管理

·SpringCloud分布式配置中心
Client       A B C
             |_|_|
               |
          ConfigServer---->本地Git仓库---->远程Git仓库
·SpringCloud Config为了微服务架构中的微服务提供集中话的外部配置支持
配置服务器为[各个不同微服务应用]的所有环节提供了[中心化的外部配置]    
·SpringCloudConfig分为服务端和客户端
 1)服务端就是Config Server, 成为分布式配置中心, 也是一个独立的微服务应用, 
 向客户端服务提供配置信息, 加密, 解密等访问接口;默认采用git来存储配置信息;
 有助于对环境配置进行版本管理, 并且可以通过git客户端工具来方便的管理和访问配置内容 
 2)客户端就是一个个需要配置的微服务, 通过制定的配置中心来管理应用资源以及
 与业务相关的配置内容, 并在启动的时候从配置中心获取和加载配置信息

· 主要功能
  1)集中管理配置文件
  2)不同环境 不同配置 动态话的配置更新 分环境部署
    比如/dev /test /prod /release 多文档块 通过" --- "就可以分割yaml配置文件
  3)运行期间动态调整配置,不在需要在每个服务部署的机器上编写配置文件,
    服务客户端会向配置中心统一拉取配置自己的信息  
  4)当配置发生变动时,符不需要重启,即可感知到配置的变化,并应用新的配置
  5)将配置信息以Rest接口的形式暴露

· 推荐的几个平台
  git/github 码云 码室(coding.net) 
  IDEA自带版本构建管理工具git/github
  
· 学会使用git和码云
1.新建仓库
2.查看码云上的git大全
 · git config --list 显示当前的配置信息
     user.email=770234358@qq.com
     user.name=KingS
   git config --global user.name="KingS"
   git config --global user.email="770234358@qq.com"
  · 在C盘用户文件夹下的.ssh文件夹下删除known_hosts文件
  · 生成密钥 ssh-keygen -t rsa -C "770234358@qq.com" 回车回车回车
  即在.ssh文件夹中生成密钥
  · 把生成的公钥内容复制到 设置 ssh中
  这样就绑定了账号跟电脑
  · 在仓库里[ssh 复制链接] 在想存放仓库的文件夹下  Git Bash here
    输入git clone 链接 下载仓库
  · 在文件夹下创建application.yaml 打开写些最简单的配置
    提交到码云上面:
    - 先进入目录(直到出现主分支 master)
    - 把当前被修改的所有文件添加到暂存区: git add .
    - 查看它的状态(文件的所有改动): git status
    - 提交到本地的同时附带消息: git commit -m "first commit"
    - 提交到远程仓库分支: git push origin[当前用户] master[要push到的分支] 
  · 查看远程文件是否增加
  
3. 新建子项目作为配置的服务端 SpringCloud_config_server_3344 
   之后的服务客户端向它请求配置信息 它负责连接远程git仓库获得配置信息 并返回给服务客户端
   · 在pom.xml中导入web启动器依赖 监控依赖actuator 以及[SpringCloudConfig配置] 
     之后整合Eureka可以导入Eureka依赖 
   · 编写它的全局配置文件(在SpringCloud中文网dalston上可以查看详细配置)
     复制仓库的http连接到配置文件里
     ## "配置服务中心"应用的 端口
     server:
       port: 3344
     ## "配置服务中心"应用的 应用服务名
     spring:
       application:
         name: springcloud-config-server
       # 连接远程的配置仓库(可以连接git 也可以连接svn)
       cloud:
         config:
           server:
             git:
               ## 这里是uri不是url
               uri: https://gitee.com/LinuxRussia/springcloud-config.git # https链接 不是ssh链接
   · 编写主启动类
   @SpringBootApplication
   @EnableConfigServer // 开启成为配置服务中心
   public class Config_Server_3344 
   · 启动测试 访问localhost:3344 有错误页面说明可以访问了
     [更新配置文件不需要重启"配置中心应用服务",刷新页面即可看到]
     当前激活的配置环境是test
     --- 读dev环境的配置: localhost:3344/application-dev.yaml
         spring:
           application:
             name: springcloud-config-dev # 显示dev环境的信息
           profiles:
             active: test # 这里显示的是当前激活的环境
     --- 读test环境的配置: localhost:3344/application-test.yaml
        spring:
          application:
            name: springcloud-config-test # 显示test环境的信息
          profiles:
            active: test # 这里显示的是当前激活的环境
     --- 错误的访问: localhost:3344/application.yaml 返回错误页
   小结: 通过config-server可以连接到git, 访问其中的资源以及配置信息
         浏览器里url请求可以通过以下几种方式获取配置信息: 
         以下的label指的是码云上面的分支, 目前只有master分支
         /{application}/{profile}/{label}====http://localhost:3344/application/dev/master
         /{application}-{profile}.yaml
         /{label}/{application}-{profile}.yaml===http://localhost:3344/master/application-dev.yaml
         /{application}-{profile}.properties
         /{label}/{application}-{profile}.properties
         当访问不符合上述或者不存在的东西,返回的配置都是空的
         SpringCloud中文网上还有安全性验证、密码等等的配置

4.配置客户端
  · 写一个配置文件config-client.yaml(配置基本信息及Eureka) push到仓库
  · 本地写一个获取配置信息的服务客户端 SpringCloud_config_client_3355
    pom.xml导入依赖导入web启动器依赖 监控依赖actuator 
    以及[这里是导入starter-config的依赖]
    <!-- 前往服务配置中心获取服务信息需要的依赖 -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
        <version>2.2.1.RELEASE</version>
    </dependency>
  · 这里项目下编写配置获取的配置文件 bootstrap.yaml 
###  它同application.yaml一样可以配SpringBoot识别
###  bootstrap.yaml表示系统级别的配置
    # 系统级别的配置信息
    spring:
      cloud:
        config:
          # 去哪里获取配置服务信息 是去"配置服务中心"应用哪里拿 而不是直接取git上拿
          uri: http://localhost:3344
          # 去哪个分支上获取资源
          label: master
          # 获取哪个配置文件 需要从git上获取的资源名称 不需要后缀(交代3344去git上取)
          name: config-client
          # 拿着个文件中的哪个生产环境
          profile: dev
          # 原来直接访问是这么访问的: http://localhost:3344/master/application-dev.yaml
###  application.yaml表示用户级别的配置
    # 用户U级别配置
    spring:
      application:
        name: SpringCloud_config_client_3355
   =====>可以在地址栏中先测试是否能访问到上述的配置
  · 编写客户端服务的主启动类
  (现在之前消费者的角色交给路由网关Zuul,
   对于配置中心来说,配置中心就是服务端,服务提供者就是客户端,由配置中心去远程拿配置)
  · 编写控制器类
  通过注入的方式获得配置信息
  显示从配置中心取得的配置信息(配置中心也是去git上取得的)
  · 启动主启动类,控制台输出:Tomcat started on port(s): 8201 [是Git上的配置文件配的]
    浏览器地址栏中访问获得配置的接口 localhost:8201/getConfig
    [要切换生产环境 直接在bootstrap.yaml里进行切换,就可以在Git上取得想要的环境配置]
    控制台输出:Tomcat started on port(s): 8202
===============>实现了配置与编码解耦============>视频21为实战(有空细看即可)
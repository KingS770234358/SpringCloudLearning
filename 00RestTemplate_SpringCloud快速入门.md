1.创建一个普通的maven项目(父项目 删除src目录)
pom.xml的一些配置
· 因为它是一个管理项目文件,所以把总文件的打包方式[设置为pom打包方式] 正常的项目这个设置为jar
<packaging>pom</packaging>
· pom.xml中引入依赖可以在<properties></properties>中统一管理依赖包的版本号
然后在下面使用SpEL表达式获取
<properties>
    <junit.version>4.13</junit.version>
    <lombok.version>1.18.10</lombok.version>
</properties>
<!-- 测试单元 -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>${junit.version}</version>
</dependency>
· 解决资源导出失败的<build></build>的配置
· 项目一些其他配置
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
    <junit.version>4.13</junit.version>
    <lombok.version>1.18.10</lombok.version>
    <log4j.version>1.2.17</log4j.version>
</properties>
· 父项目使用的是<dependencyManagement></dependencyManagement>不是正式的导入包
  子项目需要导入包的话,需要用<dependencies></dependencies>进行导入

2.创建子项目 SpringCloud_api
· pom.xml中导入依赖
· 创建数据库 并 连接数据库
# DATABASE() 读取当前数据库的名字 deptno自动增长
insert into dept (dname, db_source) values ('开发部', DATABASE())
· 创建对应的实体类[为了网络通讯 一定要序列化]
@Data
@AllArgsConstructor
@NoArgsConstructor
[@Accessors(chain=true) // 使这个类的set方法支持链式写法]
public class Dept implements Serializable {
    [// 数据库bigint 要用Long接收]
    private Long deptno;
    private String dname;
    // 看下这个数据是存在哪个数据库的字段  微服务---一个服务对应一个数据库
    // 同一个信息可能存在不同的数据库
    private String db_source;
    // 单参构造函数
    public Dept(String dname){
        this.dname = dname;
    }
}
====>这个子项目只管pojo,开始编写下个子项目(还删了它的test目录)

3.创建提供者子项目 SpringCloud_provider_dept_8001
· pom.xml中导入依赖
在pom.xml依赖中导入上述编写的子项目 才能获取pojo
<!-- 导入管理pojo实体类的子项目 -->
<dependency>
    <groupId>com.wq</groupId>
    <artifactId>SpringCloud_api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<!-- jetty 类似tomcat的一个服务器 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jetty</artifactId>
</dependency>
<!-- 热部署 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
</dependency>
· 配置
application.yaml
mybatis-config.xml
· 编写接口Mapper.java 以及对应的 Mapper.xml
· 服务层
· 控制层 提供RestFul服务 
· com.wq下创建主启动类 DeptProvider_8001
· 提供者自我测试:[测试的时候记得走8001端口]

报错1:一直无法扫描到配置文件application.yaml文件
查看StringBoot启动时的端口号,发现application.yaml文件配置没有生效
查看target文件夹下并没有application.yaml---->[pom.xml文件中静态资源过滤部分没有过滤到yaml 和 yml文件]

报错2:关于数据库中的配置 [config-location与configuration无法同时使用]
## mybatis的配置
mybatis:
  type-aliases-package: com.wq.pojo
  config-location: classpath:mybatis/mybatis-config.xml
  mapper-locations: classpath:mybatis/mapper/*.xml
  ## 数据库字段转换成驼峰的形式
#  configuration:
#   map-underscore-to-camel-case: true

4.创建消费者子项目 SpringCloud_consumer_dept_80
### 首先这个80端口很有讲究 浏览器默认访问的端口就是80端口 设置成80端口, 访问的时候就不用指定端口了
· pom.xml中导入依赖
  消费者不需要跟数据库打交道,所以不需要引入数据库的依赖
  需要引入实体类所在的子项目 以及 web依赖
  <!-- 实体类依赖 -->
  <dependency>
      <groupId>com.wq</groupId>
      <artifactId>SpringCloud_api</artifactId>
      <version>1.0-SNAPSHOT</version>
  </dependency>
  <!-- web支持 -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
  </dependency>
  <!-- 热部署 -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-devtools</artifactId>
  </dependency>
· 配置全局配置文件application.yaml 端口 80
· 要在config中配置一个RestTemplate Bean注入到Spring容器中
· 写一些消费者 [DeptConsumerController.java]=====###这里为了方便测试 使用@RestController
· 写一个自启动类进行测试DeptConsumer_80.java 因为应用端口为80端口 所以
  
  浏览器中请求
  查:http://localhost/consumer/dept/getDpetById/1
  增:http://localhost/consumer/dept/addDept?dname=教育部
  [··消费者调用提供者的addDept方法,传给提供者一个dept对象,
  请求者addDept方法参数里面一定要使用@RequestBody,否则消费者传过来的对象属性都为空
  ··重启应用生效]
  // 添加部门
  @RequestMapping("/consumer/dept/addDept")
  public Boolean addDept(Dept dept){
      System.out.println(dept);
      return restTemplate.postForObject(REST_URL_PREFIX+"/dept/addDept",dept,Boolean.class);
  }
  // 添加一个用户
  @PostMapping("/dept/addDept")
  public boolean addDept([@RequestBody] Dept dept){
      System.out.println(dept);
      return deptService.addDept(dept);
  }
  
###############################################################################
# 小结
# 也就是说,用户访问一个web应用,传统all in one 模式,整个应用的url都在同一个ip:端口下
# 无法做到说 对同一个应用 用户可以发出 不同ip:端口 请求来访问这个应用
# 只能通过应用背后的服务器之间通过这种RestFulTemplate进行http通信 转发用户的请求
###############################################################################
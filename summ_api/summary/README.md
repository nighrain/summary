

MemoZy\
java类注释模板\

`英文模板：`
=
`/**   
 *  
 * Simple to Introduction  
 * @ProjectName:  [${project_name}] 
 * @Package:      [${package_name}.${file_name}]  
 * @ClassName:    [${type_name}]   
 * @Description:  [一句话描述该类的功能]   
 * @Author:       [${user}]   
 * @CreateDate:   [${date} ${time}]   
 * @UpdateUser:   [${user}]   
 * @UpdateDate:   [${date} ${time}]   
 * @UpdateRemark: [说明本次修改内容]  
 * @Version:      [v1.0] 
 *    
 */`
--------------------- 

`中文模板：`


` /**   
  * 
  * Simple To Introduction
  * 项目名称:  [${project_name}]
  * 包:        [${package_name}]    
  * 类名称:    [${type_name}]  
  * 类描述:    [一句话描述该类的功能]
  * 创建人:    [${user}]   
  * 创建时间:  [${date} ${time}]   
  * 修改人:    [${user}]   
  * 修改时间:  [${date} ${time}]   
  * 修改备注:  [说明本次修改内容]  
  * 版本:      [v1.0]   
  *    
  */`
--------------------- 

原文：https://blog.csdn.net/fengqilove520/article/details/80264478 


---

---
`Spring Boot 打成war包的方法`
---
https://blog.csdn.net/main_958593250/article/details/79455076

Spring Boot 打成war包的方法
SpringBoot写的项目，自身嵌入了tomcat，所以可以直接运行jar包。但是，每次启动jar包创建的都是新的tomcat，这回造成上传文件丢失等问题。因此，我们需要将项目打成war包，部署到tomcat上。

修改pom.xml中的jar为war
<groupId>cn.bookcycle.panda</groupId>
<artifactId>panda-payservice</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>jar</packaging>
1
2
3
4
修改为：

<groupId>cn.bookcycle.panda</groupId>
<artifactId>panda-payservice</artifactId>
<version>0.0.1-SNAPSHOT</version>
<packaging>war</packaging>
1
2
3
4
2.在pom.xml中添加打war包的maven插件和设置打包的时候跳过单元测试代码

<plugin>
    <artifactId>maven-war-plugin</artifactId>
        <configuration>
            <!--如果想在没有web.xml文件的情况下构建WAR，请设置为false-->
            <failOnMissingWebXml>false</failOnMissingWebXml>
            <!--设置war包的名字-->
            <warName>checkroom</warName> 
        </configuration>
</plugin>

 <!-- 让打包的时候跳过测试代码 -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <skip>true</skip>
    </configuration>
</plugin>  
1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
3.在pom.xml中添加servlet包

<dependency>  
    <groupId>javax.servlet</groupId>  
    <artifactId>javax.servlet-api</artifactId>  
</dependency>
1
2
3
4
4.排除Spring Boot内置的tomcat

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
1
2
3
4
修改为

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-tomcat</artifactId>
        </exclusion>
    </exclusions>  
</dependency>
1
2
3
4
5
6
7
8
9
10
5.在main方法所属的类的同级包下，新建SpringBootStartApplication类

public class SpringBootStartApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        // 注意的Application是启动类，就是main方法所属的类
        return builder.sources(Application.class);
    }
1
2
3
4
5
6
7
6 通过eclipse开始打war包

项目右键——》Run As——》Maven Build…——》找到Goals栏，写命令clean package ——》Run

7 访问项目

最后，在项所在的文件夹文件夹里找target/*.war

将war包拷贝到Tomcat的webapps下，然后启动Tomcat

访问路径是：http://localhost:端口号/war包名/@RequestMapping中的value值

注意：

端口号是Tomcat的端口号，不是Spring Boot中配置的项目端口号

如果打包过程中遇到

[WARNING] The requested profile “pom.xml” could not be activated because it does not exist.

可以在打包的时候，清空Goals栏下面的Profiles栏的内容
--------------------- 
作者：main_958953250 
来源：CSDN 
原文：https://blog.csdn.net/main_958593250/article/details/79455076 
版权声明：本文为博主原创文章，转载请附上博文链接！
---------------



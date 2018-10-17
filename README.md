# SSM_Easy
Spring、SpringMVC、MyBatis、MySQL、Druid、Shiro、ActiveMQ、Redis、七牛云存储、阿里云，腾讯云短信、腾讯企业邮、Web Service、WebSocket、QQ登录、Face++

## 本人博客
https://blog.csdn.net/qq_24484085

## 环境信息
JDK1.8、Tomcat8.5、MySQL5.6、Redis 64Bit  
数据库：easy_data.sql  
注意：Idea 需要安装lombok插件


## 配置文件说明
* 日志　==> 　log4j.properties
* Redis　==> 　redis.properties
* 数据库　==>  　db_druid.properties
* 企业邮　==> 　email.properties
* 七牛云　==> 　kodo.properties
* MyBatis　==> 　mybatis-config.xml
* ActiveMQ　==> 　activemq.properties 
* 阿里云短信　==> 　sms_alibaba.properties
* 腾讯云短信　==> 　sms_tencent.properties


## 功能已实现
>shiro
>>地址　http://127.0.0.1:8080/user/login  
>>权限配置：spring/spring-shiro.xml(待优化)  
>> 账号   　密码   
  user	 　000000   
  admin	  　 000000     
  superadmin 　	000000  
------
>Redis
>>实现：com.iiover.util.RedisUtils  
>>demo：com.iiover.test.RedisTest
------
>七牛云存储
>>实现+demo：com.iiover.test.KodoTest
------
>阿里云短信
>>实现：com.iiover.util.SmsAlibaba  
>>demo：com.iiover.test.SmsAlibabaTest
------
>腾讯云短信
>>实现：com.iiover.util.SmsTencent  
>>demo：com.iiover.test.SmsTencentTest
------
>WebSocket
>>实现：com.iiover.test.WebSocket  
>>测试：http://www.blue-zero.com/WebSocket/  
>>测试地址：ws://127.0.0.1:8080/websocket

## 功能待实现
>ActiveMQ  

>企业邮  

>WebService  

>QQ登录

>Face++人脸识别   

>定时任务  

>PageHelper分页
------
------
## 已知Bug
>http请求 ssl




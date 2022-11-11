# 瑞吉外卖项目 Reggie_Take_Out
### 技术栈 Vue + ElementUI + SpringBoot + Mybatis Plus + Redis
### 项目视频 https://www.bilibili.com/video/BV13a411q753/

### Day1
#### 1.【解决Controller中异常处理重复写的问题】学习使用了SpringBoot的全局异常处理器
#### 2.【解决Long类型传递到前端的精度丢失问题】学习了SpringMVC中的消息转换器来解决后端主键ID为Long型，传递到前端精度丢失问题。并自己深入探究了SpringMVC中在Controller中return一个对象通过@responsebody就可以自动转化为Json对象的原理

---

### Day2
#### 1.【解决每次更新或创建员工，菜品，套餐等都需要设置更新时间创建时间问题】  学习MyBatis Plus中自动填充公共字段功能 学习使用MetaObjectHandler 学习了@TableField注解
#### 2.【解决在MetaObjectHandler中无法获取session导致无法获取当前用户的问题】 
* #### 学习使用ThreadLocal。原理：客户端每一次发送的HTTP请求，在服务器端都会分配一个新的线程来处理，在处理过程中涉及到的filter controller metaObjectHandler都属于同一个线程
* #### 因此在Filter中将用户信息存放在ThreadLocal中，在metaObjectHandler都属于同一个线程读取即可
* #### 一个可以有多个ThreadLocal对象，一个ThreadLocal对象只能存储一个值；存储多个值可以创建多个对象或者将所有值封装成一个map
* #### ThreadLocal里面的内容是线程独立的，只能被其所属线程访问
* #### 里面的内容是线程独立的，只能被其所属线程访问
#### 4. 【BUG解决】MyBatis Plus 中进行分页查询时查到的Page对象中的totalCount属性为0。忘记写MP配置类中的分页拦截器 https://blog.csdn.net/qq_38974638/article/details/119720371
#### 5.文件上传功能
* #### 前端要求：method=post,enctype=multipart/form-data,input中的type=file
* #### 后端控制器仅需要使用MultipartFile file来接收传递的文件即可，底层还是流的方式实现，基于阿帕奇的组件commons-fileUpdate 和commons-io SpringMVC框架中需要xml配置
#### 6.了解DTO的使用
#### 7.在实际应用中第一次使用了对象拷贝


---


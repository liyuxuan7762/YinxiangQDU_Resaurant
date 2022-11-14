# 🍖🍕🍦瑞吉外卖项目 Reggie_Take_Out🍔🍞🍮

### 🤖技术栈 Vue + ElementUI + SpringBoot + Mybatis Plus + Redis

### 🖥 项目视频 https://www.bilibili.com/video/BV13a411q753/

### Day1

#### 1.【⭐解决Controller中异常处理重复写的问题】学习使用了SpringBoot的全局异常处理器

#### 2.【⭐解决Long类型传递到前端的精度丢失问题】学习了SpringMVC中的消息转换器来解决后端主键ID为Long型，传递到前端精度丢失问题。

#### 并自己深入探究了SpringMVC中在Controller中return一个对象通过@responsebody就可以自动转化为Json对象的原理

---

### Day2

#### 1.【⭐解决每次更新或创建员工，菜品，套餐等都需要设置更新时间创建时间问题】 学习MyBatis Plus中自动填充公共字段功能 学习使用MetaObjectHandler 学习了@TableField注解

#### 2.【⭐解决在MetaObjectHandler中无法获取session导致无法获取当前用户的问题】

* #### 学习使用ThreadLocal。原理：客户端每一次发送的HTTP请求，在服务器端都会分配一个新的线程来处理，在处理过程中涉及到的filter controller metaObjectHandler都属于同一个线程
* #### 因此在Filter中将用户信息存放在ThreadLocal中，在metaObjectHandler都属于同一个线程读取即可
* #### 一个可以有多个ThreadLocal对象，一个ThreadLocal对象只能存储一个值；存储多个值可以创建多个对象或者将所有值封装成一个map
* #### ThreadLocal里面的内容是线程独立的，只能被其所属线程访问
* #### 里面的内容是线程独立的，只能被其所属线程访问

#### 4. 【⭐BUG解决】MyBatis Plus 中进行分页查询时查到的Page对象中的totalCount属性为0。忘记写MP配置类中的分页拦截器 https://blog.csdn.net/qq_38974638/article/details/119720371

#### 5.文件上传功能

* #### 前端要求：method=post,enctype=multipart/form-data,input中的type=file
* #### 后端控制器仅需要使用MultipartFile file来接收传递的文件即可，底层还是流的方式实现，基于阿帕奇的组件commons-fileUpdate 和commons-io SpringMVC框架中需要xml配置

#### 6.了解DTO的使用

#### 7.在实际应用中第一次使用了对象拷贝 通过BeanUtils类实现

---

### Day3

#### 1.自己实现菜品批量停售，起售；批量删除功能;

#### 2.完善套餐管理中未实现的功能

#### 3.学习使用短信验证码

#### 4.【待完善功能】 【已完善】当某个菜品已经停售的时候，其所属的套餐是否也需要停售？

#### 5.【待完善功能】 【经分析不需要完善】当某个套餐中的所有产品都起售的时候，其套餐状态是否需要修改为起售？ 不需要添加该功能：因为某些套餐可能是活动价格，当活动结束后套餐停售，但是所属菜品不应该停售

#### 6.【待完善功能】 【已完善】套餐启用了，那么套餐里面的所有菜品也应该相应的起售。

#### 7.移动端功能，登录验证码。这里由于手机登录验证码申请比较麻烦，因此通过邮箱验证码代替。

#### 8.自己实现地址管理的相关功能代码

---

#### Day4

#### 1.完成移动端菜品展示功能。

#### 2.【💡功能扩展想法】关于购物车扩展功能的想法。给购物车属性添加桌号，从而实现多个用户只要是属于同一个桌子，那么他们点的菜品共享。此外还应该在购物车记录中添加是否已经结完账的标志位，这样下次在查询的时候这些数据就不会查询出来

#### 3.完成购物车相关功能实现。这里有很多需要优化的地方

* #### 添加购物车的时候还需要判断口味，口味不同的相同菜品应该新创建一条记录

#### 4.【💪主解决问题】由于之前设置MetaDataHandler来自动填充字段中的UpdateTime,UpdateUser, CreateTime, CreateUser, 但是购物车记录中只有CreateTime,此时如果在购物车实体类的CreateTime字段使用@Table会报错，于是去网上搜索学习到MetaDataObject的HasSetter方法，进而解决这一问题

#### 5.【💡功能扩展想法】 清空购物车的做法是直接将数据删除，这一点不好。应该设置一个标志位，用户已经结账的购物记录设置一下，如果用户没付款的清空，那么就直接清空了。后续的数据可以作为营业指标分析，在后台结合Echarts前端插件可视化展示数据。

#### Day 5

#### 1.完成订单支付功能 学习了AtomicInteger类

#### 2.完成了MySQL在远程服务器上的部署

---

#### 3.学习Linux基本操作和项目部署


# log-record

通过Java注解优雅的记录操作日志，并支持SpEL表达式，自定义上下文，自定义函数，支持将日志传递至消息队列。

采用SpringBoot Starter的方式，只需要一个依赖，便可以让系统无缝支持操作日志的聚合和传递。

```
<dependency>
    <groupId>cn.monitor4all</groupId>
    <artifactId>log-record-starter</artifactId>
    <version>1.1.0</version>
</dependency>
```

最新版本号请查阅[Maven公共仓库](https://search.maven.org/artifact/cn.monitor4all/log-record-starter)

只需一句注解，日志轻松记录，不侵入业务逻辑：


```
@OperationLog(bizType = "addressChange", bizId = "#request.orderId", msg = "'用户' + #queryUserName(#request.userId) + '修改了订单的配送地址：从' + #oldAddress + '修改到' + #queryOldAddress(#request.orderId)")
public Response<T> function(Request request) {
  // 业务执行逻辑
}
```



## 项目背景

大家一定见过下图的操作日志：

![](pic/sample1.png)

![](pic/sample2.png)

在代码层面，如何优雅的记录上面的日志呢？

一下子能想到最简单的方式，**封装一个操作日志记录类**（例如LogUtil）。 例子如下：

```
String template = "用户%s修改了订单的配送地址：从“%s”修改到“%s”"
LogUtil.log(orderNo, String.format(tempalte, "小明", "金灿灿小区", "银盏盏小区"),  "小明")
```

这种方式会导致业务代码被记录日志的代码侵入，**对于代码的可读性和可维护性来说是一个灾难。**

这个方式显然不够优雅，让我们试试使用注解：

```
@OperationLog(bizType = "addressChange", bizId = "20211102001", msg = "用户 小明 修改了订单的配送地址：从 金灿灿小区 修改到 银盏盏小区")
public Response<T> function(Request request) {
  // 业务执行逻辑
}
```

日志的记录被放到了注解，对业务代码没有侵入。

但是新的问题来了，我们该如何把**订单ID，用户信息，数据库里的旧地址，函数入参的新地址传递给注解呢？**

Spring的SpEL表达式（Spring Expression Language）可以帮助我们，通过引入SpEL表达式，我们可以获取函数的入参。这样我们就可以对上面的注解进行修改：

- 订单ID：#request.orderId
- 新地址"银盏盏小区"：#request.newAddress

```
@OperationLog(bizType = "addressChange", bizId = "#request.orderId", msg = "'用户 小明 修改了订单的配送地址：从 金灿灿小区 修改到' + #request.newAddress")
public Response<T> function(Request request) {
  // 业务执行逻辑
}
```

如此一来，订单ID和地址的新值就可以通过解析入参动态获取了。

然而，用户信息，以及老的配送地址，是需要业务代码去获取的，**入参里并不会包含这些数据。**

解决方案也不是没有，我们创建一个日志上下文LogRecordContext，**让用户手动传递代码中计算出来的值，再交给SpEL解析。**

```
@OperationLog(bizType = "addressChange", bizId = "#request.orderId", msg = "'用户' + #userName + '修改了订单的配送地址：从' + #oldAddress + '修改到' + #request.newAddress")
public Response<T> function(Request request) {
  // 业务执行逻辑
  ...
  // 手动传递日志上下文：用户信息 地址旧值
  LogRecordContext.putVariables("userName", queryUserName(request.getUserId()));
  LogRecordContext.putVariables("oldAddress", queryOldAddress(request.getOrderId()));
}
```

什么？你说这不就又侵入了业务逻辑了么？

确实是的，虽然能用，**但是对于有“强迫症”的同学，这样的实现还是不够优雅，我们用自定义函数，解决这个问题。**

SpEL支持在表达式中传入用户自定义函数，我们将queryUserName和queryOldAddress这两个函数传递给SpEL，SpEL在解析表达式时，会自动执行对应函数。

最终，我们的注解变成了这样，并且最终记录了日志：

```
@OperationLog(bizType = "addressChange", bizId = "#request.orderId", msg = "'用户' + #queryUserName(#request.userId) + '修改了订单的配送地址：从' + #oldAddress + '修改到' + #queryOldAddress(#request.orderId)")
public Response<T> function(Request request) {
  // 业务执行逻辑
}
```

> 用户 小明 修改了订单的配送地址：从 金灿灿小区 修改到 银盏盏小区

以上便是本库的大致实现原理。


## 项目介绍

本仓库帮助你通过注解优雅地聚合项目中的操作日志，对业务代码无侵入。

此外，你可以方便地将所有日志推送到下列数据管道：

1. 本地处理
2. 发送至RabbitMQ
3. 发送至RocketMQ
4. 发送至SpringCloud Stream

日志内包含：

```
logId：生成的UUID
bizId：业务唯一ID（支持SpEL）
bizType：业务类型
exception：函数执行失败时写入异常信息
operateDate：操作执行时间
success：函数是否执行成功
msg：操作日志主体信息（支持SpEL）
tag：用户自定义标签
returnStr: 方法执行成功后的返回值（JSON）
executionTime：方法执行耗时（毫秒）
extra：额外信息（支持SpEL）
operatorId：操作人ID
```

本项目特点：

- 快速接入：使用Spring Boot Starter实现，用户直接在pom.xml引入依赖即可使用
- SpEL解析：支持SpEL表达式
- 自定义上下文：支持手动传递键值对，通过SpEL进行解析
- 自定义函数：支持注册自定义函数，通过SpEL进行解析
- SPI拓展实现：操作人ID获取，日志本地监听
- 支持重复注解


## 使用方法

### 接入方式

**只需要简单的三步：**

**第一步：** SpringBoot项目中引入依赖

最新版本号请查阅Maven公共仓库：

https://search.maven.org/artifact/cn.monitor4all/log-record-starter

```
<dependency>
    <groupId>cn.monitor4all</groupId>
    <artifactId>log-record-starter</artifactId>
    <version>{最新版本号}</version>
</dependency>
```

**推荐使用>=1.1.0版本**

**第二步：** 添加数据源配置

支持推送日志数据至：

1. 本地应用监听
2. RabbitMQ 
3. RocketMQ
4. SpringCloud Stream

**1. 本地应用监听**

若只需要在同一应用内处理日志信息，只需要实现接口IOperationLogGetService，便可对日志进行处理。

```java
public class CustomFuncTestOperationLogGetService implements IOperationLogGetService {
    @Override
    public void createLog(LogDTO logDTO) {
        log.info("logDTO: [{}]", JSON.toJSONString(logDTO));
    }
}
```



**2. RabbitMQ**

配置好RabbitMQ的发送者

```
log-record.data-pipeline=rabbitMq
log-record.rabbit-mq-properties.host=localhost
log-record.rabbit-mq-properties.port=5672
log-record.rabbit-mq-properties.username=admin
log-record.rabbit-mq-properties.password=xxxxxx
log-record.rabbit-mq-properties.queue-name=logRecord
log-record.rabbit-mq-properties.routing-key=
log-record.rabbit-mq-properties.exchange-name=logRecord
```

**3. RocketMQ**

配置好RocketMQ的发送者

```
log-record.data-pipeline=rocketMq
log-record.rocket-mq-properties.topic=logRecord
log-record.rocket-mq-properties.tag=
log-record.rocket-mq-properties.group-name=logRecord
log-record.rocket-mq-properties.namesrv-addr=localhost:9876
```

**4. Stream**

配置好 stream 

```
log-record.data-pipeline=stream
log-record.stream.destination=logRecord
log-record.stream.group=logRecord
# 为空时 默认为spring.cloud.stream.default-binder指定的Binder
log-record.stream.binder=
# rocketmq binder例子
spring.cloud.stream.rocketmq.binder.name-server=127.0.0.1:9876
spring.cloud.stream.rocketmq.binder.enable-msg-trace=false
```

**第三步：** 在需要记录系统操作的方法上，添加注解

```
@OperationLog(bizType = "addressChange", bizId = "20211102001", msg = "用户 小明 修改了订单的配送地址：从 金灿灿小区 修改到 银盏盏小区")
public Response<T> function(Request request) {
  // 业务执行逻辑
}
```

## 进阶使用

### 全局操作人ID获取

大部分情况下，操作人ID往往不会在方法参数中传递，更多会是查询集团内BUC信息、查询外部服务、查表等获取。所以开放了SPI，只需要实现接口IOperationLogGetService，便可以统一注入操作人ID。

```java
public class OperationLogGetService implements IOperatorIdGetService {

    @Override
    public String getOperatorId() {
        // 查询操作人信息
        return "张三";
    }
}
```

注意：若实现了接口后仍在注解手动传入OperatorID，则以传入的OperatorID优先。




### 自定义传递上下文

直接引入类LogRecordContext，放入键值对。

```
@OperationLog(bizType = "addressChange", bizId = "#request.orderId", msg = "'用户' + #userName + '修改了订单的配送地址：从' + #oldAddress + '修改到' + #request.newAddress")
public Response<T> function(Request request) {
  // 业务执行逻辑
  ...
  // 手动传递日志上下文：用户信息 地址旧值
  LogRecordContext.putVariables("userName", queryUserName(request.getUserId()));
  LogRecordContext.putVariables("oldAddress", queryOldAddress(request.getOrderId()));
}
```

### 自定义函数

将@LogRecordFunc注解申明在需要注册到SpEL的自定义函数上。

注意，需要在类上也声明@LogRecordFunc，否则无法找到该函数。

@LogRecordFunc可以添加参数value，实现自定义方法别名

```
@LogRecordFunc("test")
public class CustomFunctionService {

    @LogRecordFunc("testMethodWithCustomName")
    public static String testMethodWithCustomName(){
        return "testMethodWithCustomName";
    }

    @LogRecordFunc
    public static String testMethodWithoutCustomName(){
        return "testMethodWithoutCustomName";
    }

}
```


```
@OperationLog(bizId = "#test_testMethodWithCustomName()", bizType = "testMethodWithCustomName")
@OperationLog(bizId = "#test_testMethodWithoutCustomName()", bizType = "testMethodWithoutCustomName")
public void testCustomFunc() {
}
```

### 自定义SpEL解析顺序

在默认配置下，注解切面的逻辑在方法执行之后才会执行，这样会带来一个问题，如果在方法内部修改了方法参数，SpEL解析后取值就变成了改变后的值。

可以使用LogRecordContext写入旧值，避免这个问题，只是有一定代码侵入性。

为了满足一些特殊需求，注解中提供boolean参数executeBeforeFunc，若设置为true，则会在方法执行前先解析SpEL参数。 这样也会带来负作用，方法内写入的数值，比如自定义上下文，就不再参与SpEL解析了。

### 重复注解

```
@OperationLog(bizId = "#testClass.testId", bizType = "testType1", msg = "#testFunc(#testClass.testId)")
@OperationLog(bizId = "#testClass.testId", bizType = "testType2", msg = "#testFunc(#testClass.testId)")
@OperationLog(bizId = "#testClass.testId", bizType = "testType3", msg = "'用户将旧值' + #old + '更改为新值' + #testClass.testStr")
```

我们还加上了重复注解的支持，可以在一个方法上同时加多个@OperationLog，会保证@OperationLog顺序，下图是最终使用效果：

![](https://tva1.sinaimg.cn/large/008i3skNly1gw5oyjdc2uj31js0u0133.jpg)

### 消息分发线程池配置

在组装好logDTO后，默认使用线程池对消息进行分发，发送至本地监听函数或者消息队列发送者。

可以使用如下配置修改线程池大小

```
log-record.pool-size=4（默认为4）
```


## 应用场景

以下罗列了一些实际的应用场景，包括我业务中实际使用，并且已经上线使用的场景。

### 操作日志

如最上面一张CRM系统的图描述的那样，在用户进行了编辑操作后，拿到用户操作的数据，执行日志写入。

### 系统日志

操作日志是主要的功能，当然也可以兼顾一些系统日志记录的操作，比如只是想简单记录方法执行时间，出入参等，也可以通过该库轻松做到。

### 通知

应用之间通过关键操作的日志消息，互相通知

### 跨应用数据聚合

在多个应用中，如果需要做行为相同的业务逻辑，完全可以在各个系统中通过该库将数据发送到同一个消息队列中，再进行统一处理。


## 附录：Demo

最后，肯定有小伙伴希望有一个完整的使用Demo，这就奉上！

完整客户端Demo项目:

https://github.com/qqxx6661/systemLog

## Release Note

[Release](https://github.com/qqxx6661/logRecord/releases)

## 配套教程文章

如何使用注解优雅的记录操作日志

https://mp.weixin.qq.com/s/q2qmffH8t-ou2apOa6BiPQ

如何提交自己的项目到Maven公共仓库

https://mp.weixin.qq.com/s/B9LA6be_cPAKACbZot_Nrg

## 关注我

公众号：后端技术漫谈

全网博客名：蛮三刀酱
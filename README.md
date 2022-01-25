# log-record

## 项目背景

大家一定见过下图的操作日志：

![](https://p1.meituan.net/travelcube/ae35fb1babaab193c1dd0b1bbbe9f07d96643.png)

在代码层面，如何优雅的实现上面的日志记录呢？ 

你可能会一下子想到最简单的方式，通过**封装一个操作日志记录类**（例如LogUtil）。 使用一个用户修改配送地址的操作举例：

```
String template = "用户%s修改了订单的配送地址：从“%s”修改到“%s”"
LogUtil.log(orderNo, String.format(tempalte, "小明", "金灿灿小区", "银盏盏小区"),  "小明")
```

这种方式会导致业务逻辑被记录日志的代码侵入，**对于代码的可读性和可维护性来说是一个灾难。** 

这个方式显然不优雅，让我们试试使用注解：

```
@OperationLog(bizType = "addressChange", bizId = "20211102001", msg = "用户 小明 修改了订单的配送地址：从 金灿灿小区 修改到 银盏盏小区")
public Response<T> function(Request request) {
  // 业务执行逻辑
}
```

可以看到，这样日志的记录被放到了注解，对业务代码没有了侵入。 但是新的问题来了，**我们该如何把订单ID，用户信息，数据库里的旧地址，函数入参的新地址传递给注解呢？**

Spring的SpEL表达式（Spring Expression Language）可以帮助我们，通过引入SpEL表达式，我们可以获取函数的入参。这样我们就可以对上面的注解进行修改：

- 订单ID：#request.orderId
- 新地址"银盏盏小区"：#request.newAddress

```
@OperationLog(bizType = "addressChange", bizId = "#request.orderId", msg = "'用户 小明 修改了订单的配送地址：从 金灿灿小区 修改到' + #request.newAddress")
public Response<T> function(Request request) {
  // 业务执行逻辑
}
```

这样依赖，订单ID和地址的新值就可以通过解析入参动态获取了，但是事情还没有结束，我们的用户信息，以及老的配送地址，是需要业务代码去获取的，入参里并不会包含这些数据。

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

确实是的，虽然能用，**但是对于有“强迫症”的同学，这样的实现还是不够优雅，接下来我们用自定义函数，解决这个问题。**

SpEL支持在表达式中传入用户自定义函数，我们将queryUserName和queryOldAddress这两个函数传递给SpEL，SpEL在解析表达式时，会自动执行对应函数。

最终，我们的注解变成了这样，并且最终记录了日志：

```
@OperationLog(bizType = "addressChange", bizId = "#request.orderId", msg = "'用户' + #queryUserName(#request.userId) + '修改了订单的配送地址：从' + #oldAddress + '修改到' + #queryOldAddress(#request.orderId)")
public Response<T> function(Request request) {
  // 业务执行逻辑
}
```

> 用户 小明 修改了订单的配送地址：从 金灿灿小区 修改到 银盏盏小区


## 项目介绍

本仓库帮助你通过注解优雅地聚合项目中的操作日志，对业务代码无侵入。

此外，你可以方便地将所有日志推送到下列数据管道：

1. 本地监听处理
2. 发送至RabbitMQ
3. 发送至RocketMQ
4. SpringCloud Stream

日志内包含：

```
logId：生成的UUID
bizId：业务唯一ID
bizType：业务类型
exception：函数执行失败时写入异常信息
operateDate：操作执行时间
success：函数是否执行成功
msg：注解中传递的msg（JSON）
tag：用户自定义标签
returnStr: 方法执行成功后的返回值（JSON）
executionTime：方法执行耗时（毫秒）
```

本项目特点：

- 方便接入：使用Spring Boot Starter实现，用户直接在pom.xml引入依赖，快速接入
- SpEL解析：直接写表达式解析入参
- 自定义上下文：支持手动传递键值对，通过SpEL进行解析
- 自定义函数：支持注册自定义函数，通过SpEL进行解析



## 使用方法

### 接入方式

**只需要简单的三步：**

**第一步：** SpringBoot项目中引入依赖（最新版本号请查阅Maven公共仓库）

```
<dependency>
    <groupId>cn.monitor4all</groupId>
    <artifactId>log-record-starter</artifactId>
    <version>1.0.4</version>
</dependency>
```

**第二步：** 添加数据源配置

支持推送日志数据至：

1. 本地应用监听
2. RabbitMQ 
3. RocketMQ
4. SpringCloud Stream

**1. 本地应用监听**

若只需要在同一应用内处理日志信息，只需要继承抽象类CustomLogListener，便可对日志进行处理。

```
@Slf4j
@Component
public class TestCustomLogListener extends CustomLogListener {

    @Override
    public void createLog(LogDTO logDTO) throws Exception {
        log.info("TestCustomLogListener 本地接收到日志 [{}]", logDTO);
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

**第三步：** 在你自己的项目中，在需要记录日志的方法上，添加@OperationLog注解。

```
@OperationLog(bizType = "orderCreate", bizId = "#request.orderId", msg = "#request")
public Response<BaseResult> function(Request request) {
  // 业务执行逻辑
}
```

- （必填）bizType：业务类型
- （必填）bizId：唯一业务ID（支持SpEL表达式）
- （非必填）msg：需要传递的其他数据（支持SpEL表达式）
- （非必填）tag：自定义标签


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

```
@LogRecordFunc
public class MyFuction {

    private static TestService testService;

    @LogRecordFunc
    public static String testFunc(String str) {
        if (testService == null) {
            testService = SpringContextUtils.getBean(TestService.class);
        }
        return testService.testServiceFunc2(str);
    }
}
```


```
@OperationLog(bizType = "addressChange", bizId = "#request.orderId", msg = "'用户' + #queryUserName(#request.userId) + '修改了订单的配送地址：从' + #oldAddress + '修改到' + #queryOldAddress(#request.orderId)")
public Response<T> function(Request request) {
  // 业务执行逻辑
}
```

### 重复注解

```
@OperationLog(bizId = "#testClass.testId", bizType = "testType1", msg = "#testFunc(#testClass.testId)")
@OperationLog(bizId = "#testClass.testId", bizType = "testType2", msg = "#testFunc(#testClass.testId)")
@OperationLog(bizId = "#testClass.testId", bizType = "testType3", msg = "'用户将旧值' + #old + '更改为新值' + #testClass.testStr")
```

我们还加上了重复注解的支持，可以在一个方法上同时加多个@OperationLog，下图是最终使用效果：

![](https://tva1.sinaimg.cn/large/008i3skNly1gw5oyjdc2uj31js0u0133.jpg)


## 实现原理

由于采用的是SpringBoot Starter方式，会自动扫描到依赖包中的类，并自动通过Spring进行配置和管理。

该注解通过在切面中解析SpEL参数，将数据发往数据源。发送的消息体如下：

方法处理正常发送消息体：

```
[LogDTO(logId=3771ff1e-e5ff-4251-a534-31dab5b666b3, bizId=str, bizType=testType1, exception=null, operateDate=Sat Nov 06 20:08:54 CST 2021, success=true, msg={"testList":["1","2","3"],"testStr":"str"}, tag=operation)]
```

方法处理异常发送消息体：

```
[LogDTO(logId=d162b2db-2346-4144-8cd4-aea900e4682b, bizId=str, bizType=testType1, exception=testError, operateDate=Sat Nov 06 20:09:24 CST 2021, success=false, msg={"testList":["1","2","3"],"testStr":"str"}, tag=operation)]
```


## 应用场景

以下罗列了一些实际的应用场景，包括我业务中实际使用，并且已经上线使用的场景。

一、操作日志：如最上面一张CRM系统的图描述的那样，在用户进行了编辑操作后，拿到用户操作的数据，执行日志写入。

二、通知触发：由于我的业务是接手了好几个仓库，并且这几个仓库的操作串成了一条完成链路，我需要在链路的某个节点触发给用户的提醒，如果写硬编码也可以实现，但是远不如在方法上使用注解发送消息来得方便。例如下方在下单方法调用后发送消息。

![](https://tva1.sinaimg.cn/large/008i3skNly1gw5oyoktakj326i06wwgk.jpg)

三、数据表双写：我的业务中，几个系统互相吞吐数据，订单的一部分数据存留在外部系统里，我们最终目标想要将其中一个系统替代掉，所以需要拦截他们的数据，将数据请求拦截一层，并将拦截的方法使用该二方库进行全部参数的发送，将数据同步写入我们自己的数据库中，实现”双写“。

![](https://tva1.sinaimg.cn/large/008i3skNly1gw5oyrcuqyj31zi056gn6.jpg)

四、跨应用数据聚合：和”三“类似，在多个应用中，如果需要做行为相同的业务逻辑，完全可以在各个系统中将数据发送到同一个消息队列中，再进行统一处理。

## 附录：Demo

最后，肯定有小伙伴希望有一个完整的使用Demo，这就奉上！

完整Demo项目:

https://github.com/qqxx6661/systemLog


## 配套教程文章

如何使用注解优雅的记录操作日志

https://mp.weixin.qq.com/s/q2qmffH8t-ou2apOa6BiPQ

如何提交自己的项目到Maven公共仓库

https://mp.weixin.qq.com/s/B9LA6be_cPAKACbZot_Nrg

## 关注我

公众号：后端技术漫谈

全网博客名：蛮三刀酱
# log-record

本项目支持用户使用注解的方式从方法中获取操作日志，并推送到指定数据源。

支持推送日志数据至：

- 本地应用监听
- RabbitMQ
- RocketMQ

**只需要简单的加上一个@OperationLog便可以将方法的参数，返回结果甚至是异常堆栈通过消息队列发送出去，统一处理。**

```
@OperationLog(bizType = "orderCreate", bizId = "#request.orderId", msg = "#request")
public Response<BaseResult> function(Request request) {
  // 方法执行逻辑
}
```

### 使用方法

**只需要简单的三步：**

**第一步：** SpringBoot项目中引入依赖

```
<dependency>
    <groupId>cn.monitor4all</groupId>
    <artifactId>log-record-starter</artifactId>
    <version>1.0.3</version>
</dependency>
```

**第二步：** 在Spring配置文件中添加数据源配置

目前支持RabbitMq和RocketMq

未来会接其他数据源。RabbitMq和RocketMq的安装在这里不展开了，实在是不想把篇幅拉得太大，大家可以自行谷歌下，比如“Docker安装RabbitMq”类似的文章，几分钟就可以设置安装好。


```
#log-record.data-pipeline=rabbitMq
log-record.data-pipeline=rocketMq

# 若使用rabbitMq
log-record.rabbit-mq-properties.host=localhost
log-record.rabbit-mq-properties.port=5672
log-record.rabbit-mq-properties.username=admin
log-record.rabbit-mq-properties.password=xxxxxx
log-record.rabbit-mq-properties.queue-name=logRecord
log-record.rabbit-mq-properties.routing-key=
log-record.rabbit-mq-properties.exchange-name=logRecord

# 若使用rocketMq
log-record.rocket-mq-properties.topic=logRecord
log-record.rocket-mq-properties.tag=
log-record.rocket-mq-properties.group-name=logRecord
log-record.rocket-mq-properties.namesrv-addr=localhost:9876
```

**第三步：** 在你自己的项目中，在需要记录日志的方法上，添加注解。

```
@OperationLog(bizType = "orderCreate", bizId = "#request.orderId", msg = "#request")
public Response<BaseResult> function(Request request) {
  // 方法执行逻辑
}
```

- （必填）bizType：业务类型
- （必填）bizId：唯一业务ID（支持SpEL表达式）
- （非必填）msg：需要传递的其他数据（支持SpEL表达式）
- （非必填）tag：自定义标签

### 代码工作原理

由于采用的是SpringBoot Starter方式，会自动扫描到依赖包中的类，并自动通过Spring进行配置和管理。

该注解通过在切面中解析SpEL参数（啥事SpEL？快去谷歌下），将数据发往数据源。发送的消息体如下：

方法处理正常发送消息体：

```
[LogDTO(logId=3771ff1e-e5ff-4251-a534-31dab5b666b3, bizId=str, bizType=testType1, exception=null, operateDate=Sat Nov 06 20:08:54 CST 2021, success=true, msg={"testList":["1","2","3"],"testStr":"str"}, tag=operation)]
```

方法处理异常发送消息体：

```
[LogDTO(logId=d162b2db-2346-4144-8cd4-aea900e4682b, bizId=str, bizType=testType1, exception=testError, operateDate=Sat Nov 06 20:09:24 CST 2021, success=false, msg={"testList":["1","2","3"],"testStr":"str"}, tag=operation)]
```

LogDTO是定义的消息结构：

```
logId：生成的UUID
bizId：注解中传递的bizId
bizType：注解中传递的bizType
exception：若方法执行失败，写入执行的异常信息
operateDate:操作执行的当前时间
success：方式是否执行成功
msg：注解中传递的msg（JSON化）
tag：注解中传递的tag
returnStr: 方法执行成功后的返回值（JSON化）
```

我还加上了重复注解的支持，可以在一个方法上同时加多个@OperationLog，下图是最终使用效果，可以看到，有几个@OperationLog，就能同时发送多条日志：

![](https://tva1.sinaimg.cn/large/008i3skNly1gw5oyjdc2uj31js0u0133.jpg)

**项目具体的实现原理和细节，放在下一篇文章详细讲。**（肯定会填坑）

### 应用场景

以下罗列了一些实际的应用场景，包括我业务中实际使用，并且已经上线使用的场景。

一、特定操作记录日志：如文章最上面一张CRM系统的图描述的那样，在用户进行了编辑操作后，拿到用户操作的数据，执行日志写入。

二、特定操作触发通知：由于我的业务是接手了好几个仓库，并且这几个仓库的操作串成了一条完成链路，我需要在链路的某个节点触发给用户的提醒，如果写硬编码也可以实现，但是远不如在方法上使用注解发送消息来得方便。例如下方在下单方法调用后发送消息。

![](https://tva1.sinaimg.cn/large/008i3skNly1gw5oyoktakj326i06wwgk.jpg)

三、特定操作更新数据表：我的业务中，几个系统互相吞吐数据，订单的一部分数据存留在外部系统里，我们最终目标想要将其中一个系统替代掉，所以需要拦截他们的数据，恰好几个系统是使用LINK作为网关的，我们将数据请求拦截一层，并将拦截的方法使用该二方库进行全部参数的发送，将数据同步写入我们自己的数据库中，实现”双写“。

![](https://tva1.sinaimg.cn/large/008i3skNly1gw5oyrcuqyj31zi056gn6.jpg)

四、跨多应用数据聚合操作：和”三“类似，在多个应用中，如果需要做行为相同的业务逻辑，完全可以在各个系统中将数据发送到同一个消息队列中，再进行统一处理。

### 附录：Demo

最后，肯定有小伙伴希望有一个完整的使用Demo，这就奉上！

完整Demo项目:

https://github.com/qqxx6661/systemLog

log-record-starter:

https://github.com/qqxx6661/logRecord


## 配套文章

如何使用注解优雅的记录操作日志 |《萌新学开源》01

https://mp.weixin.qq.com/s/q2qmffH8t-ou2apOa6BiPQ

## 关注我

公众号：后端技术漫谈

全网博客名：蛮三刀酱
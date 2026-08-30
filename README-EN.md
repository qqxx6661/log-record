# log-record

[![CI](https://img.shields.io/github/actions/workflow/status/qqxx6661/log-record/ci.yml?branch=master&logo=github&logoColor=white)](https://github.com/qqxx6661/log-record/actions/workflows/ci.yml)
[![Codecov](https://img.shields.io/codecov/c/github/qqxx6661/log-record?logo=codecov&logoColor=white)](https://codecov.io/gh/qqxx6661/log-record/branch/master)
[![Maven Central](https://img.shields.io/maven-central/v/cn.monitor4all/log-record-starter?logo=apache-maven&logoColor=white)](https://search.maven.org/artifact/cn.monitor4all/log-record-starter)
[![License](https://img.shields.io/github/license/qqxx6661/log-record?color=4D7A97&logo=apache)](https://www.apache.org/licenses/LICENSE-2.0.html)

English translation of the original README — quick summary and usage guide for English-speaking contributors and users.

## What is this

log-record is a lightweight Java library (Spring Boot starter) to elegantly record operation logs, system logs and backend events using annotations. It supports SpEL expressions, custom context and functions, object diffing, configurable pipelines (local handler, RabbitMQ, RocketMQ, Spring Cloud Stream), retry and fallback, and more — all with minimal intrusion into business code.

## Key features

- Easy integration: Spring Boot starter, single dependency.
- Non-intrusive: annotation-based, safe if logging fails.
- SpEL support: compose dynamic log fields using method args and custom functions.
- Object Diff: compute differences between old/new objects (even different classes).
- Conditional logging: only record logs when SpEL condition holds.
- Custom context and custom functions for SpEL.
- Pluggable data pipeline: local handler, RabbitMQ, RocketMQ, Spring Cloud Stream.
- Multiple annotations per method supported; execution order preserved.
- Retry and fallback handling with SPI hooks.
- Configurable thread pool for async log delivery.
- Option to record or skip method return values.

## Quick start

Add the starter dependency to your Spring Boot project.

Spring Boot 1 & 2 (JDK 8+):
```xml
<dependency>
  <groupId>cn.monitor4all</groupId>
  <artifactId>log-record-starter</artifactId>
  <version>{latest-version}</version>
</dependency>
```

Spring Boot 3 (JDK 17+):
```xml
<dependency>
  <groupId>cn.monitor4all</groupId>
  <artifactId>log-record-springboot3-starter</artifactId>
  <version>{latest-version}</version>
</dependency>
```

(See Maven Central for the latest version.)

## Basic usage

Annotate methods with @OperationLog and use SpEL in annotation attributes:

```java
@OperationLog(bizType = "'followerChange'",
              bizId = "#request.orderId",
              msg = "'User ' + #queryUserName(#request.userId) + ' changed follower: from ' + #queryOldFollower(#request.orderId) + ' to ' + #request.newFollower")
public Response<T> changeFollower(Request request) {
    // business logic
}
```

You can register custom SpEL functions (annotated with @LogRecordFunc) to call from the annotation expressions, or put values into LogRecordContext for expressions to reference.

## Data pipeline options

- Local handler (implement IOperationLogGetService#createLog to process logs in-app).
- RabbitMQ: configure `log-record.data-pipeline=rabbitMq` and RabbitMQ properties.
- RocketMQ: configure `log-record.data-pipeline=rocketMq` and RocketMQ properties.
- Spring Cloud Stream: configure `log-record.data-pipeline=stream` and stream properties.

Example: local handler implementation
```java
@Component
public class CustomOperationLogGetService implements IOperationLogGetService {
    @Override
    public boolean createLog(LogDTO logDTO) {
        log.info("logDTO: [{}]", JSON.toJSONString(logDTO));
        return true;
    }
}
```

## Advanced features (high-level)

- SpEL usage and caveats (string literals must be quoted: use "'text'").
- executeBeforeFunc: parse SpEL before method execution when needed.
- Built-in SpEL parameters: `_return`, `_errorMsg` (only available after method executes).
- Built-in custom function `_DIFF` for object diffing.
- Register custom functions with @LogRecordFunc (static methods preferred).
- Global operator ID via SPI (IOperatorIdGetService).
- Retry and error handling via configuration and LogRecordErrorHandlerService SPI.
- Thread pool configuration for asynchronous delivery.
- Option to include or exclude method return value in logs (recordReturnValue).

## Object diff

Annotate classes/fields with @LogRecordDiffObject / @LogRecordDiffField to control diff aliases and ignored fields. Use `_DIFF(old, new)` in SpEL to include a diff in msg/extra fields. Configuration options for ignoring nulls and customizing output format are available.

## Spring Boot 3 differences

Due to tightened reflection access in newer JDKs, Spring Boot 3 cannot obtain parameter names via reflection — use `#p0`, `#p1`, ... positional names in SpEL expressions for method arguments.

## Demo and tests

- Unit tests include many usage examples.
- Full demo projects: https://github.com/qqxx6661/systemLog

## Release & links

- Releases: https://github.com/qqxx6661/log-record/releases
- Original inspiration: Meituan technical blog (linked in original README)
- Articles:
  - How to elegantly record operation logs with annotations: https://mp.weixin.qq.com/s/q2qmffH8t-ou2apOa6BiPQ
  - How to publish to Maven Central: https://mp.weixin.qq.com/s/B9LA6be_cPAKACbZot_Nrg

## Contributing

Contributions and PRs are welcome. Please follow existing code style and include tests for new features.

## License

Apache-2.0 — see LICENSE.

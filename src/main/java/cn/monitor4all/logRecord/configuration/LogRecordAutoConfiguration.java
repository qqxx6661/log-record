package cn.monitor4all.logRecord.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan("cn.monitor4all.logRecord")
@Import({
        LogRecordConfiguration.class,
        RabbitMqSenderConfiguration.class,
        RocketMqSenderConfiguration.class,
        StreamSenderConfiguration.class
})
public class LogRecordAutoConfiguration {

}

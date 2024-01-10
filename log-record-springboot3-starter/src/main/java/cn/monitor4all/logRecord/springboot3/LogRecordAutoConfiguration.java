package cn.monitor4all.logRecord.springboot3;

import cn.monitor4all.logRecord.configuration.RabbitMqSenderConfiguration;
import cn.monitor4all.logRecord.configuration.RocketMqSenderConfiguration;
import cn.monitor4all.logRecord.configuration.StreamSenderConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@ComponentScan("cn.monitor4all.logRecord")
@Import({RabbitMqSenderConfiguration.class, RocketMqSenderConfiguration.class, StreamSenderConfiguration.class})
public class LogRecordAutoConfiguration {

}

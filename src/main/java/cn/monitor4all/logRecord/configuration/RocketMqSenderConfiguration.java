package cn.monitor4all.logRecord.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author yangzhendong
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "log-record.data-pipeline", havingValue = "rocketMq")
@EnableConfigurationProperties({LogRecordProperties.class})
public class RocketMqSenderConfiguration {

    private String rabbitHost;
    private int rabbitPort;
    private String exchange;
    private String queue;
    private String routingKey;
    private String username;
    private String password;

    @Autowired
    private LogRecordProperties properties;

    @PostConstruct
    public void rabbitMqConfig() {
        this.rabbitHost = properties.getRocketMqProperties().getHost();
        this.rabbitPort = properties.getRocketMqProperties().getPort();
        this.queue = properties.getRocketMqProperties().getQueueName();
        this.routingKey = properties.getRocketMqProperties().getRoutingKey();
        this.exchange= properties.getRocketMqProperties().getExchangeName();
        this.username= properties.getRocketMqProperties().getUsername();
        this.password= properties.getRocketMqProperties().getPassword();
        log.info("LogRecord RocketMqSenderConfiguration host [{}] port [{}] exchange [{}] queue [{}] routingKey [{}]",
                rabbitHost, rabbitPort, exchange, queue, routingKey);
    }
}

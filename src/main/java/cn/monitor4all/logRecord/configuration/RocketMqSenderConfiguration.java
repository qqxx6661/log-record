package cn.monitor4all.logRecord.configuration;

import cn.monitor4all.logRecord.constans.LogConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author yangzhendong
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "log-record.data-pipeline", havingValue = LogConstants.DataPipeline.ROCKET_MQ)
@EnableConfigurationProperties({LogRecordProperties.class})
public class RocketMqSenderConfiguration {

    private String namesrvAddr;
    private String groupName;
    private int maxMessageSize;
    private int sendMsgTimeout;
    private int retryTimesWhenSendFailed;
    private String topic;

    @Autowired
    private LogRecordProperties properties;

    @PostConstruct
    public void rabbitMqConfig() {
        this.namesrvAddr = properties.getRocketMqProperties().getNamesrvAddr();
        this.groupName = properties.getRocketMqProperties().getGroupName();
        this.maxMessageSize = properties.getRocketMqProperties().getMaxMessageSize();
        this.sendMsgTimeout = properties.getRocketMqProperties().getSendMsgTimeout();
        this.retryTimesWhenSendFailed = properties.getRocketMqProperties().getRetryTimesWhenSendFailed();
        this.topic = properties.getRocketMqProperties().getTopic();
        log.info("LogRecord RocketMqSenderConfiguration namesrvAddr [{}] groupName [{}] maxMessageSize [{}] " +
                        "sendMsgTimeout [{}] retryTimesWhenSendFailed [{}] topic [{}]",
                namesrvAddr, groupName, maxMessageSize, sendMsgTimeout, retryTimesWhenSendFailed, topic);
    }

    @Bean
    public DefaultMQProducer defaultMQProducer() throws RuntimeException {
        DefaultMQProducer producer = new DefaultMQProducer(this.groupName);
        producer.setNamesrvAddr(this.namesrvAddr);
        producer.setCreateTopicKey(this.topic);
        // 如果需要同一个 jvm 中不同的 producer 往不同的 mq 集群发送消息，需要设置不同的 instanceName
        //producer.setInstanceName(instanceName);
        // 如果发送消息的最大限制 默认为0
        producer.setMaxMessageSize(this.maxMessageSize);
        // 如果发送消息超时时间 默认为3000
        producer.setSendMsgTimeout(this.sendMsgTimeout);
        // 如果发送消息失败，设置重试次数，默认为2
        producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);
        try {
            producer.start();
            log.info("LogRecord Rocket producer is started");
        } catch (MQClientException e) {
            log.error("LogRecord failed to start Rocket producer", e);
            throw new RuntimeException(e);
        }
        return producer;
    }
}

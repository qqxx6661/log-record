package cn.monitor4all.logRecord.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yangzhendong
 */
@Data
@ConfigurationProperties(prefix = "log-record", ignoreUnknownFields = false)
public class LogRecordProperties {

    private RabbitMqProperties rabbitMqProperties;

    private RocketMqProperties rocketMqProperties;

    /**
     * choose pipeline for message: rabbitMq, rocketMq
     */
    private String dataPipeline;

    @Data
    public static class RabbitMqProperties {
        private String host;
        private int port;
        private String username;
        private String password;
        private String queueName;
        private String exchangeName;
        private String routingKey;
    }

    @Data
    public static class RocketMqProperties {
        private String host;
        private int port;
        private String username;
        private String password;
        private String queueName;
        private String exchangeName;
        private String routingKey;
    }
}

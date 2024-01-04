package cn.monitor4all.logRecord.configuration;

import cn.monitor4all.logRecord.function.CustomFunctionObjectDiff;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "log-record")
public class LogRecordProperties {

    private RabbitMqProperties rabbitMqProperties;

    private RocketMqProperties rocketMqProperties;

    private StreamProperties stream;

    private ThreadPoolProperties threadPool = new ThreadPoolProperties();

    private String dataPipeline;

    private String diffMsgFormat = CustomFunctionObjectDiff.DEFAULT_DIFF_MSG_FORMAT;

    private String diffMsgSeparator = CustomFunctionObjectDiff.DEFAULT_DIFF_MSG_SEPARATOR;

    private RetryProperties retry = new RetryProperties();

    private Boolean diffIgnoreOldObjectNullValue = false;

    private Boolean diffIgnoreNewObjectNullValue = false;

    @Data
    public static class RetryProperties {

        /**
         * 日志处理失败重试次数
         */
        private int retryTimes = 0;
    }

    @Data
    public static class ThreadPoolProperties {

        private int poolSize = 4;

        private boolean enabled = true;
    }

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
        private String topic = "logRecord";
        private String tag = "";
        private String namesrvAddr = "localhost:9876";
        private String groupName = "logRecord";
        private int maxMessageSize = 4000000;
        private int sendMsgTimeout = 3000;
        private int retryTimesWhenSendFailed = 2;
    }

    @Data
    public static class StreamProperties {
        /**
         * 默认对应消息中间件topic rocketmq的topic, RabbitMq的 exchangeName
         */
        private String destination;

        /**
         * 默认对应的分组
         */
        private String group;

        /**
         * 默认的binder（对应的消息中间件）
         */
        private String binder;
    }

}

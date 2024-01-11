package cn.monitor4all.logRecord.configuration;

import cn.monitor4all.logRecord.constants.LogConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.config.BindingProperties;
import org.springframework.cloud.stream.config.BindingServiceConfiguration;
import org.springframework.cloud.stream.config.BindingServiceProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Optional;


@Slf4j
@Configuration
@ConditionalOnProperty(name = "log-record.data-pipeline", havingValue = LogConstants.DataPipeline.STREAM)
@EnableConfigurationProperties({LogRecordProperties.class})
@ConditionalOnClass(BindingServiceConfiguration.class)
@AutoConfigureBefore({BindingServiceConfiguration.class})
@EnableBinding(StreamSenderConfiguration.LogRecordChannel.class)
public class StreamSenderConfiguration {

    @Value("${spring.application.name:}")
    private String applicationName;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private final LogRecordProperties properties;

    private final BindingServiceProperties bindings;

    public StreamSenderConfiguration(BindingServiceProperties bindings, LogRecordProperties logRecordProperties) {
        this.properties = logRecordProperties;
        this.bindings = bindings;
    }

    @PostConstruct
    public void init() {
        BindingProperties inputBinding = this.bindings.getBindings().get(LogRecordChannel.OUTPUT);
        if (inputBinding == null) {
            this.bindings.getBindings().put(LogRecordChannel.OUTPUT, new BindingProperties());
        }

        BindingProperties input = this.bindings.getBindings().get(LogRecordChannel.OUTPUT);
        if (input.getDestination() == null || input.getDestination().equals(LogRecordChannel.OUTPUT)) {
            input.setDestination(Optional.ofNullable(properties.getStream().getDestination()).orElse("stream_logging_" + applicationName + "_" + activeProfile ));
        }
        if (!StringUtils.hasText(input.getGroup())) {
            input.setGroup(Optional.ofNullable(properties.getStream().getGroup()).orElse(applicationName));
        }

        if (StringUtils.hasText(properties.getStream().getBinder())) {
            input.setBinder(properties.getStream().getBinder());
        }


    }

    public interface LogRecordChannel {

        String OUTPUT = "LogRecordChannel";

        /**
         * 日志输出
         * @return SubscribableChannel messageLoggingQueueInput();
         */
        @Output(OUTPUT)
        MessageChannel messageLoggingQueueInput();

    }
}

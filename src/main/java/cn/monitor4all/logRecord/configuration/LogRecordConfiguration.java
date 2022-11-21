package cn.monitor4all.logRecord.configuration;

import cn.monitor4all.logRecord.thread.LogRecordThreadWrapper;
import cn.monitor4all.logRecord.function.CustomFunctionRegistrar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LogRecordConfiguration {

    @Bean
    public CustomFunctionRegistrar registrar() {
        return new CustomFunctionRegistrar();
    }


    @Bean
    @ConditionalOnMissingBean
    public LogRecordThreadWrapper createLogConsumer() {
        return new LogRecordThreadWrapper(){};
    }
}

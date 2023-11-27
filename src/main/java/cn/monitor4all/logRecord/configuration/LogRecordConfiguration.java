package cn.monitor4all.logRecord.configuration;

import cn.monitor4all.logRecord.function.CustomFunctionRegistrar;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class LogRecordConfiguration {

    @Bean
    public CustomFunctionRegistrar logrRecordRegistrar() {
        return new CustomFunctionRegistrar();
    }

}

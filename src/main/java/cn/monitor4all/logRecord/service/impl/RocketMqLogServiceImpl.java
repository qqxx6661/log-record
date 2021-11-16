package cn.monitor4all.logRecord.service.impl;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.configuration.LogRecordProperties;
import cn.monitor4all.logRecord.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableConfigurationProperties({LogRecordProperties.class})
@ConditionalOnProperty(name = "log-record.data-pipeline", havingValue = "rocketMq")
public class RocketMqLogServiceImpl implements LogService {

    @Autowired
    private LogRecordProperties properties;

    @Override
    public boolean createLog(LogDTO logDTO) {
        log.info("RocketMq ready to send routingKey [{}] LogDTO [{}]", properties.getRocketMqProperties().getRoutingKey(), logDTO);
        // TODO
        return true;
    }
}

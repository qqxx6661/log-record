package cn.monitor4all.logRecord.service.impl;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.configuration.LogRecordProperties;
import cn.monitor4all.logRecord.constants.LogConstants;
import cn.monitor4all.logRecord.service.DataPipelineService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableConfigurationProperties({LogRecordProperties.class})
@ConditionalOnProperty(name = "log-record.data-pipeline", havingValue = LogConstants.DataPipeline.RABBIT_MQ)
public class RabbitMqDataPipelineServiceImpl implements DataPipelineService {

    @Autowired
    private RabbitTemplate rubeExchangeTemplate;

    @Autowired
    private LogRecordProperties properties;

    @Override
    public boolean createLog(LogDTO logDTO) {
        log.info("LogRecord RabbitMq ready to send routingKey [{}] LogDTO [{}]", properties.getRabbitMq().getRoutingKey(), logDTO);
        rubeExchangeTemplate.convertAndSend(properties.getRabbitMq().getRoutingKey(), JSON.toJSONString(logDTO));
        return true;
    }
}

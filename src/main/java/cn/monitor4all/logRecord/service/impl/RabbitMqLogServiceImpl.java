package cn.monitor4all.logRecord.service.impl;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.configuration.LogRecordProperties;
import cn.monitor4all.logRecord.constans.LogConstans;
import cn.monitor4all.logRecord.service.LogService;
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
@ConditionalOnProperty(name = "log-record.data-pipeline", havingValue = LogConstans.DataPipeline.RABBIT_MQ)
public class RabbitMqLogServiceImpl implements LogService {

    @Autowired
    private RabbitTemplate rubeExchangeTemplate;

    @Autowired
    private LogRecordProperties properties;

    @Override
    public boolean createLog(LogDTO logDTO) {
        log.info("LogRecord RabbitMq ready to send routingKey [{}] LogDTO [{}]", properties.getRabbitMqProperties().getRoutingKey(), logDTO);
        // 消息队列处理逻辑
        rubeExchangeTemplate.convertAndSend(properties.getRabbitMqProperties().getRoutingKey(), JSON.toJSONString(logDTO));
        return true;
    }
}

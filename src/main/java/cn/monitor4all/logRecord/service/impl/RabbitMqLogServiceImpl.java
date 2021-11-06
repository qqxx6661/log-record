package cn.monitor4all.logRecord.service.impl;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.service.LogService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMqLogServiceImpl implements LogService {

    @Autowired
    private RabbitTemplate rubeExchangeTemplate;

    @Value("${log-record.rabbitmq.routing-key}")
    private String routingKey;

    @Override
    public boolean createLog(LogDTO logDTO) {
        log.info("Ready to send routingKey [{}] LogDTO [{}]", routingKey, logDTO);
        // 消息队列处理逻辑
        rubeExchangeTemplate.convertAndSend(routingKey, JSON.toJSONString(logDTO));
        return true;
    }
}

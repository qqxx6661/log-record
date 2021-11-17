package cn.monitor4all.logRecord.service.impl;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.configuration.LogRecordProperties;
import cn.monitor4all.logRecord.service.LogService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
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

    @Autowired
    private DefaultMQProducer defaultMQProducer;

    @Override
    public boolean createLog(LogDTO logDTO) throws Exception {
        log.info("LogRecord RocketMq ready to send topic [{}] tag [{}] LogDTO [{}]", properties.getRocketMqProperties().getTopic(), properties.getRocketMqProperties().getTag(), logDTO);
        Message msg = new Message(properties.getRocketMqProperties().getTopic(), properties.getRocketMqProperties().getTag(), (JSON.toJSONString(logDTO)).getBytes(RemotingHelper.DEFAULT_CHARSET));
        SendResult sendResult = defaultMQProducer.send(msg);
        log.info("LogRecord RocketMq sendResult: [{}]",sendResult);
        return true;
    }
}

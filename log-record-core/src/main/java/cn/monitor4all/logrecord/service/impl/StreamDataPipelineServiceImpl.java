package cn.monitor4all.logRecord.service.impl;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.configuration.LogRecordProperties;
import cn.monitor4all.logRecord.configuration.StreamSenderConfiguration;
import cn.monitor4all.logRecord.constants.LogConstants;
import cn.monitor4all.logRecord.service.DataPipelineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@EnableConfigurationProperties({LogRecordProperties.class})
@ConditionalOnProperty(name = "log-record.data-pipeline", havingValue = LogConstants.DataPipeline.STREAM)
public class StreamDataPipelineServiceImpl implements DataPipelineService {

    @Autowired
    private StreamSenderConfiguration.LogRecordChannel channel;

    @Override
    public boolean createLog(LogDTO logDTO) {
        return channel.messageLoggingQueueInput().send(MessageBuilder.withPayload(logDTO).build());
    }
}

package cn.monitor4all.logRecord.test.service;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.service.IOperationLogGetService;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestComponent;

@Slf4j
@TestComponent
@ConditionalOnProperty(name = "test.config", havingValue = "normal")
public class OperationLogGetService implements IOperationLogGetService {

    @Override
    public boolean createLog(LogDTO logDTO) {
        log.info("logDTO: [{}]", JSON.toJSONString(logDTO));

        if ("testBizIdWithSpEL".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getBizId(), "1");
        }
        if ("testBizIdWithRawString".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getBizId(), "2");
        }

        if ("testTagWithSpEL".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getTag(), "tag1");
        }
        if ("testTagWithRawString".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getTag(), "tag2");
        }

        if ("testRecordReturnValueTrue".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getReturnStr(), "\"returnStr\"");
        }

        if ("testRecordReturnValueFalse".equals(logDTO.getBizType())) {
            Assertions.assertNull(logDTO.getReturnStr());
        }

        if ("testReturnObject".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getReturnStr(), "{\"id\":1,\"name\":\"张三\"}");
        }

        if ("testException".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getException(), "testException");
        }

        if ("testMsgAndExtraWithSpEL".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getMsg(), "将旧值张三更改为新值李四");
            Assertions.assertEquals(logDTO.getExtra(), "将旧值张三更改为新值李四");
        }
        if ("testMsgAndExtraWithRawString".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getMsg(), "str");
            Assertions.assertEquals(logDTO.getExtra(), "str");
        }
        if ("testMsgAndExtraWithObject".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getMsg(), "{\"id\":1,\"name\":\"name\"}");
            Assertions.assertEquals(logDTO.getExtra(), "{\"id\":1,\"name\":\"name\"}");
        }

        if ("testStaticMethodWithCustomName".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getBizId(), "testStaticMethodWithCustomName");
        }
        if ("testStaticMethodWithoutCustomName".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getBizId(), "testStaticMethodWithoutCustomName");
        }

        if ("testOperatorIdWithSpEL".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getOperatorId(), "001");
        }
        if ("testOperatorIdWithCustomOperatorIdGetService".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getOperatorId(), "操作人");
        }

        if ("testExecuteBeforeFunc1".equals(logDTO.getBizType())) {
            Assertions.assertNull(logDTO.getTag());
        }
        if ("testExecuteAfterFunc".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getTag(), "value");
        }
        if ("testExecuteBeforeFunc2".equals(logDTO.getBizType())) {
            Assertions.assertNull(logDTO.getTag());
        }

        if ("testLogRecordDiffField".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getMsg(), "【用户工号】从【1】变成了【2】 【name】从【张三】变成了【李四】");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassName(), "cn.monitor4all.logRecord.test.bean.TestUser");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassAlias(), "用户信息实体");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getFieldName(), "id");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldFieldAlias(), "用户工号");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewFieldAlias(), "用户工号");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldValue(), 1);
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewValue(), 2);
        }

        if ("testLogRecordDiffObject".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getMsg(), "【用户工号】从【1】变成了【2】 【name】从【张三】变成了【 】 【age】从【 】变成了【20】 【nickNameList】从【[小张三, 大张三]】变成了【[小李四, 大李四]】");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassName(), "cn.monitor4all.logRecord.test.bean.TestComplexUser");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassAlias(), "用户信息复杂实体");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getFieldName(), "id");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldFieldAlias(), "用户工号");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewFieldAlias(), "用户工号");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldValue(), 1);
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewValue(), 2);
        }

        if ("testLogRecordDiffIgnoreField".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getMsg(), "【用户工号】从【1】变成了【2】 【name】从【张三】变成了【 】 【age】从【 】变成了【20】 【nickNameList】从【[小张三, 大张三]】变成了【[小李四, 大李四]】");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassName(), "cn.monitor4all.logRecord.test.bean.TestComplexUser");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassAlias(), "用户信息复杂实体");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getFieldName(), "id");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldFieldAlias(), "用户工号");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewFieldAlias(), "用户工号");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldValue(), 1);
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewValue(), 2);
        }

        if ("testLogRecordDiffNestedClass".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getMsg(), "【id】从【2】变成了【3】 【name】从【小张三】变成了【小李四】" +
                    " 【jobList】从【[TestDiffJob(jobId=22, jobName=222, dutyList=[TestDiffDuty(dutyId=222, dutyName=222)])]】" +
                    "变成了【[TestDiffJob(jobId=22, jobName=222, dutyList=[TestDiffDuty(dutyId=333, dutyName=222)])]】");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassName(), "cn.monitor4all.logRecord.test.bean.diff.TestDiffUserVO");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassAlias(), "用户信息嵌套展示实体");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getNewClassName(), "cn.monitor4all.logRecord.test.bean.diff.TestDiffUserParam");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getNewClassAlias(), "用户信息嵌套入参实体");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getFieldName(), "id");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldValue(), 2);
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewValue(), 3);
        }

        if ("testMultipleDiff".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getMsg(), "第一个DIFF：【用户工号】从【1】变成了【2】 【name】从【张三】变成了【李四】第二个DIFF【用户工号】从【3】变成了【2】 【name】从【王五】变成了【李四】");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassName(), "cn.monitor4all.logRecord.test.bean.TestUser");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getOldClassAlias(), "用户信息实体");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getFieldName(), "id");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldFieldAlias(), "用户工号");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewFieldAlias(), "用户工号");
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getOldValue(), 1);
            Assertions.assertEquals(logDTO.getDiffDTOList().get(0).getDiffFieldDTOList().get(0).getNewValue(), 2);
        }

        if ("testCondition1".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getBizId(), "1");
        }
        if ("testCondition2".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getBizId(), "2");
        }

        if ("testCustomSuccess1".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getSuccess(), true);
        }
        if ("testCustomSuccess2".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getSuccess(), false);
        }
        if ("testCustomSuccess3".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getSuccess(), true);
        }

        if ("testDefaultParamReturn".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getSuccess(), true);
            Assertions.assertEquals(logDTO.getMsg(), "{\"id\":1,\"name\":\"张三\"}");
        }
        if ("testDefaultParamErrorMsg".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getSuccess(), false);
            Assertions.assertEquals(logDTO.getMsg(), "exception");
        }

        if ("testConstantWithSpEL".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getTag(), "type1");
        }
        if ("testEnumWithSpEL1".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getTag(), "TYPE1");
        }
        if ("testEnumWithSpEL2".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getTag(), "type1");
        }
        if ("testEnumWithSpEL3".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getTag(), "枚举1");
        }

        if ("testLogRecordContext".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getMsg(), "customValue");
        }

        if ("testMapUseInLogRecordContext".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getMsg(), "{\"customKey\":\"customValue\"}");
        }

        if ("testMsgWithSpELNull".equals(logDTO.getBizType())) {
            Assertions.assertNull(logDTO.getMsg());
        }

        if ("testExtraWithSpELNull".equals(logDTO.getBizType())) {
            Assertions.assertNull(logDTO.getExtra());
        }

        if ("testSpringBeanCustomFuncNoParam".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getMsg(), "【用户工号】从【1】变成了【2】 【name】从【asd】变成了【dsa】");
        }

        if ("testSpringBeanCustomFuncWithParam".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getMsg(), "【用户工号】从【3】变成了【2】 【name】从【DSA】变成了【dsa】");
        }

        if ("testSpringBeanCustomFuncNoReturn".equals(logDTO.getBizType())) {
            Assertions.assertNull(logDTO.getMsg());
        }

        if ("testLogRecordThreadWrapper".equals(logDTO.getBizType())) {
            Assertions.assertEquals(logDTO.getExtra(), "extraInfo");
        }

        return true;
    }
}

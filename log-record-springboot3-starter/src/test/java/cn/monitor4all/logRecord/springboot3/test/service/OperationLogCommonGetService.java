package cn.monitor4all.logRecord.springboot3.test.service;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.context.LogRecordContext;
import cn.monitor4all.logRecord.service.IOperationLogGetService;
import cn.monitor4all.logRecord.springboot3.test.utils.TestHelper;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestComponent;

@Slf4j
@TestComponent
@ConditionalOnProperty(name = "test.config", havingValue = "common")
public class OperationLogCommonGetService implements IOperationLogGetService {

    @Override
    public boolean createLog(LogDTO logDTO) {
        log.info("logDTO: [{}]", JSON.toJSONString(logDTO));

        if ("testBizIdWithSpEL".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testBizIdWithSpEL", logDTO);
            TestHelper.releaseLock("testBizIdWithSpEL");
        }

        if ("testBizIdWithRawString".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testBizIdWithRawString", logDTO);
            TestHelper.releaseLock("testBizIdWithRawString");
        }

        if ("testTagWithSpEL".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testTagWithSpEL", logDTO);
            TestHelper.releaseLock("testTagWithSpEL");
        }

        if ("testTagWithRawString".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testTagWithRawString", logDTO);
            TestHelper.releaseLock("testTagWithRawString");
        }

        if ("testMsgWithSpEL".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testMsgWithSpEL", logDTO);
            TestHelper.releaseLock("testMsgWithSpEL");
        }

        if ("testMsgWithSpELNull".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testMsgWithSpELNull", logDTO);
            TestHelper.releaseLock("testMsgWithSpELNull");
        }

        if ("testMsgWithRawString".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testMsgWithRawString", logDTO);
            TestHelper.releaseLock("testMsgWithRawString");
        }

        if ("testMsgWithObject".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testMsgWithObject", logDTO);
            TestHelper.releaseLock("testMsgWithObject");
        }

        if ("testExtraWithSpEL".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testExtraWithSpEL", logDTO);
            TestHelper.releaseLock("testExtraWithSpEL");
        }

        if ("testExtraWithSpELNull".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testExtraWithSpELNull", logDTO);
            TestHelper.releaseLock("testExtraWithSpELNull");
        }

        if ("testExtraWithRawString".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testExtraWithRawString", logDTO);
            TestHelper.releaseLock("testExtraWithRawString");
        }
        if ("testExtraWithObject".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testExtraWithObject", logDTO);
            TestHelper.releaseLock("testExtraWithObject");
        }

        if ("testRecordReturnValueTrue".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testRecordReturnValueTrue", logDTO);
            TestHelper.releaseLock("testRecordReturnValueTrue");
        }

        if ("testRecordReturnValueFalse".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testRecordReturnValueFalse", logDTO);
            TestHelper.releaseLock("testRecordReturnValueFalse");
        }

        if ("testReturnObjectToJson".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testReturnObjectToJson", logDTO);
            TestHelper.releaseLock("testReturnObjectToJson");

        }

        if ("testMethodThrowException".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testMethodThrowException", logDTO);
            TestHelper.releaseLock("testMethodThrowException");
        }

        if ("testStaticMethodWithCustomName".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testStaticMethodWithCustomName", logDTO);
            TestHelper.releaseLock("testStaticMethodWithCustomName");
        }

        if ("testStaticMethodWithoutCustomName".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testStaticMethodWithoutCustomName", logDTO);
            TestHelper.releaseLock("testStaticMethodWithoutCustomName");
        }

        if ("testOperatorIdWithSpEL".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testOperatorIdWithSpEL", logDTO);
            TestHelper.releaseLock("testOperatorIdWithSpEL");
        }

        if ("testOperatorIdWithCustomOperatorIdGetService".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testOperatorIdWithCustomOperatorIdGetService", logDTO);
            TestHelper.releaseLock("testOperatorIdWithCustomOperatorIdGetService");
        }

        if ("testExecuteBeforeFunc".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testExecuteBeforeFunc", logDTO);
            TestHelper.releaseLock("testExecuteBeforeFunc");
        }

        if ("testExecuteAfterFunc".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testExecuteAfterFunc", logDTO);
            TestHelper.releaseLock("testExecuteAfterFunc");
        }

        if ("testLogRecordDiffField".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testLogRecordDiffField", logDTO);
            TestHelper.releaseLock("testLogRecordDiffField");
        }

        if ("testLogRecordDiffObject".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testLogRecordDiffObject", logDTO);
            TestHelper.releaseLock("testLogRecordDiffObject");
        }

        if ("testLogRecordDiffIgnoreField".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testLogRecordDiffIgnoreField", logDTO);
            TestHelper.releaseLock("testLogRecordDiffIgnoreField");
        }

        if ("testLogRecordDiffNestedClass".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testLogRecordDiffNestedClass", logDTO);
            TestHelper.releaseLock("testLogRecordDiffNestedClass");
        }

        if ("testMultipleDiff".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testMultipleDiff", logDTO);
            TestHelper.releaseLock("testMultipleDiff");
        }

        if ("testExtendClassDiff".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testExtendClassDiff", logDTO);
            TestHelper.releaseLock("testExtendClassDiff");
        }

        if ("testConditionTrue".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testConditionTrue", logDTO);
            TestHelper.releaseLock("testConditionTrue");
        }

        if ("testConditionFalse".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testConditionFalse", logDTO);
            TestHelper.releaseLock("testConditionFalse");
        }

        if ("testCustomSuccessTrue".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testCustomSuccessTrue", logDTO);
            TestHelper.releaseLock("testCustomSuccessTrue");
        }

        if ("testCustomSuccessFalse".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testCustomSuccessFalse", logDTO);
            TestHelper.releaseLock("testCustomSuccessFalse");
        }

        if ("testDefaultParamReturn".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testDefaultParamReturn", logDTO);
            TestHelper.releaseLock("testDefaultParamReturn");
        }

        if ("testDefaultParamErrorMsg".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testDefaultParamErrorMsg", logDTO);
            TestHelper.releaseLock("testDefaultParamErrorMsg");
        }

        if ("testConstantWithSpEL".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testConstantWithSpEL", logDTO);
            TestHelper.releaseLock("testConstantWithSpEL");
        }

        if ("testEnumWithSpEL1".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testEnumWithSpEL1", logDTO);
            TestHelper.releaseLock("testEnumWithSpEL1");
        }

        if ("testEnumWithSpEL2".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testEnumWithSpEL2", logDTO);
            TestHelper.releaseLock("testEnumWithSpEL2");
        }

        if ("testEnumWithSpEL3".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testEnumWithSpEL3", logDTO);
            TestHelper.releaseLock("testEnumWithSpEL3");
        }

        if ("testSpELInLogRecordContext".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testSpELInLogRecordContext", logDTO);
            TestHelper.releaseLock("testSpELInLogRecordContext");
        }

        if ("testMapUseInLogRecordContext".equals(logDTO.getBizType())) {
            TestHelper.putLogDTO("testMapUseInLogRecordContext", logDTO);
            TestHelper.releaseLock("testMapUseInLogRecordContext");
        }

        if ("testLogRecordContextTransmittableThreadLocal".equals(logDTO.getBizType())) {
            // 在createLog中操作LogRecordContext
            logDTO.setMsg(LogRecordContext.getVariable("customKey").toString());
            TestHelper.putLogDTO("testLogRecordContextTransmittableThreadLocal", logDTO);
            TestHelper.releaseLock("testLogRecordContextTransmittableThreadLocal");
        }

        return true;
    }
}

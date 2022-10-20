package cn.monitor4all.logRecord.function;


import cn.monitor4all.logRecord.annotation.LogRecordDiffField;
import cn.monitor4all.logRecord.annotation.LogRecordDiffObject;
import cn.monitor4all.logRecord.annotation.LogRecordFunc;
import cn.monitor4all.logRecord.bean.DiffDTO;
import cn.monitor4all.logRecord.bean.DiffFieldDTO;
import cn.monitor4all.logRecord.configuration.LogRecordProperties;
import cn.monitor4all.logRecord.context.LogRecordContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@LogRecordFunc
@EnableConfigurationProperties(value = LogRecordProperties.class)
public class CustomFunctionObjectDiff {

    public static final String DEFAULT_DIFF_MSG_FORMAT = "【${_fieldName}】从【${_oldValue}】变成了【${_newValue}】";
    public static final String DEFAULT_DIFF_MSG_SEPARATOR = " ";
    public static final String DEFAULT_DIFF_NULL_TEXT = " ";

    private static String DIFF_MSG_FORMAT;
    private static String DIFF_MSG_SEPARATOR;

    public CustomFunctionObjectDiff(LogRecordProperties logRecordProperties){
        DIFF_MSG_FORMAT = logRecordProperties.getDiffMsgFormat().equals(DEFAULT_DIFF_MSG_FORMAT) ? DEFAULT_DIFF_MSG_FORMAT : new String(logRecordProperties.getDiffMsgFormat().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        DIFF_MSG_SEPARATOR = logRecordProperties.getDiffMsgSeparator().equals(DEFAULT_DIFF_MSG_SEPARATOR) ? DEFAULT_DIFF_MSG_SEPARATOR : new String(logRecordProperties.getDiffMsgSeparator().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        log.info("CustomFunctionObjectDiff init diffMsgFormat [{}] diffMsgSeparator [{}]", DIFF_MSG_FORMAT, DIFF_MSG_SEPARATOR);
    }

    /**
     * 默认的DIFF方法实现
     * @return DIFF日志消息文案
     */
    @LogRecordFunc("_DIFF")
    public static String objectDiff(Object oldObject, Object newObject) {
        StringBuilder msg = new StringBuilder();

        // 若包含null对象，直接返回
        if (oldObject == null || newObject == null) {
            log.warn("null object found [{}] [{}]", oldObject, newObject);
            return msg.toString();
        }

        // 类注解获取
        String oldClassName = oldObject.getClass().getName();
        String newClassName = newObject.getClass().getName();
        LogRecordDiffObject oldObjectLogRecordDiff = oldObject.getClass().getDeclaredAnnotation(LogRecordDiffObject.class);
        LogRecordDiffObject newObjectLogRecordDiff = newObject.getClass().getDeclaredAnnotation(LogRecordDiffObject.class);

        // 类全部字段DIFF开关
        boolean oldClassEnableAllFields = oldObjectLogRecordDiff != null && oldObjectLogRecordDiff.enableAllFields();
        log.debug("oldClassEnableAllFields [{}]", oldClassEnableAllFields);

        // 类别名处理
        String oldClassAlias = oldObjectLogRecordDiff != null && StringUtils.isNotBlank(oldObjectLogRecordDiff.alias()) ? oldObjectLogRecordDiff.alias() : null;
        String newClassAlias = newObjectLogRecordDiff != null && StringUtils.isNotBlank(newObjectLogRecordDiff.alias()) ? newObjectLogRecordDiff.alias() : null;
        log.debug("oldClassName [{}] oldClassAlias [{}] newClassName [{}] newClassAlias [{}]", oldClassName, oldClassAlias, newClassName, newClassAlias);

        // 新旧字段Map和新旧字段别名Map
        Map<String, String> oldFieldAliasMap = new LinkedHashMap<>();
        Map<String, String> newFieldAliasMap = new LinkedHashMap<>();
        Map<String, Object> oldValueMap = new LinkedHashMap<>();
        Map<String, Object> newValueMap = new LinkedHashMap<>();

        // 遍历旧对象
        Field[] fields = oldObject.getClass().getDeclaredFields();
        for (Field oldField : fields) {
            try {
                LogRecordDiffField oldFieldLogRecordDiff = oldField.getDeclaredAnnotation(LogRecordDiffField.class);
                // 若没有打开所有对象DIFF开关且没有LogRecordDiff注解则跳过本次循环
                if (!oldClassEnableAllFields && oldFieldLogRecordDiff == null) {
                    continue;
                }
                try {
                    // 在新对象中寻找同名字段，若找不到则跳过本次循环
                    Field newField = newObject.getClass().getDeclaredField(oldField.getName());
                    LogRecordDiffField newFieldLogRecordDiff = newField.getDeclaredAnnotation(LogRecordDiffField.class);
                    if (oldFieldLogRecordDiff != null && newFieldLogRecordDiff != null) {
                        String oldFieldAlias = StringUtils.isNotBlank(oldFieldLogRecordDiff.alias()) ? oldFieldLogRecordDiff.alias() : null;
                        String newFieldAlias = StringUtils.isNotBlank(newFieldLogRecordDiff.alias()) ? newFieldLogRecordDiff.alias() : null;
                        oldFieldAliasMap.put(oldField.getName(), oldFieldAlias);
                        newFieldAliasMap.put(newField.getName(), newFieldAlias);
                        log.debug("field [{}] has annotation oldField alias [{}] newField alias [{}]", oldField.getName(), oldFieldAlias, newFieldAlias);
                    }
                    oldField.setAccessible(true);
                    newField.setAccessible(true);
                    Object oldValue = oldField.get(oldObject);
                    Object newValue = newField.get(newObject);
                    boolean diff = (oldValue == null && newValue != null)
                            || (oldValue != null && newValue == null)
                            || (oldValue != null && newValue != null && !oldValue.equals(newValue));
                    if (diff) {
                        log.debug("field [{}] is different between oldObject [{}] newObject [{}]", oldField.getName(), oldValue, newValue);
                        oldValueMap.put(oldField.getName(), oldValue);
                        newValueMap.put(newField.getName(), newValue);
                    }
                } catch (NoSuchFieldException e) {
                    log.info("no field named [{}] in newObject, skip", oldField.getName());
                }
            } catch (Exception e) {
                log.error("objectDiff error", e);
            }
        }

        // DIFF后组装DiffDTOList和msg
        List<String> diffMsgList = new ArrayList<>();
        log.debug("oldFieldAliasMap [{}]", oldFieldAliasMap);
        log.debug("newFieldAliasMap [{}]", newFieldAliasMap);
        log.debug("oldValueMap [{}]", oldValueMap);
        log.debug("newValueMap [{}]", newValueMap);
        DiffDTO diffDTO = new DiffDTO();
        diffDTO.setOldClassName(oldClassName);
        diffDTO.setOldClassAlias(oldClassAlias);
        diffDTO.setNewClassName(newClassName);
        diffDTO.setNewClassAlias(newClassAlias);
        List<DiffFieldDTO> diffFieldDTOList = new ArrayList<>();
        for (Map.Entry<String, Object> entry: oldValueMap.entrySet()) {
            String fieldName = entry.getKey();
            Object oldValue = entry.getValue();
            Object newValue = newValueMap.getOrDefault(entry.getKey(), null);
            String oldFieldAlias = oldFieldAliasMap.getOrDefault(entry.getKey(), null);
            String newFieldAlias = newFieldAliasMap.getOrDefault(entry.getKey(), null);
            DiffFieldDTO diffFieldDTO = new DiffFieldDTO();
            diffFieldDTO.setFieldName(fieldName);
            diffFieldDTO.setOldFieldAlias(oldFieldAlias);
            diffFieldDTO.setNewFieldAlias(newFieldAlias);
            diffFieldDTO.setOldValue(oldValue);
            diffFieldDTO.setNewValue(newValue);
            diffFieldDTOList.add(diffFieldDTO);
            // 默认使用旧对象的字段名或别名
            Map<String, Object> valuesMap = new HashMap<>(3);
            valuesMap.put("_fieldName", StringUtils.isNotBlank(oldFieldAlias) ? oldFieldAlias : fieldName);
            valuesMap.put("_oldValue", ObjectUtils.isEmpty(oldValue) ? DEFAULT_DIFF_NULL_TEXT : oldValue.toString());
            valuesMap.put("_newValue", ObjectUtils.isEmpty(newValue) ? DEFAULT_DIFF_NULL_TEXT : newValue.toString());
            StringSubstitutor sub = new StringSubstitutor(valuesMap);
            diffMsgList.add(sub.replace(DIFF_MSG_FORMAT));
        }
        diffDTO.setDiffFieldDTOList(diffFieldDTOList);
        msg.append(String.join(DIFF_MSG_SEPARATOR, diffMsgList));
        LogRecordContext.addDiffDTO(diffDTO);
        return msg.toString();
    }

}

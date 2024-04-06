package cn.monitor4all.logRecord.function;


import cn.monitor4all.logRecord.annotation.LogRecordDiffField;
import cn.monitor4all.logRecord.annotation.LogRecordDiffObject;
import cn.monitor4all.logRecord.annotation.LogRecordFunc;
import cn.monitor4all.logRecord.bean.DiffDTO;
import cn.monitor4all.logRecord.bean.DiffFieldDTO;
import cn.monitor4all.logRecord.configuration.LogRecordProperties;
import cn.monitor4all.logRecord.context.LogRecordContext;
import cn.monitor4all.logRecord.exception.LogRecordException;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
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

    private static boolean diffIgnoreOldObjectNullValue;
    private static boolean diffIgnoreNewObjectNullValue;

    public CustomFunctionObjectDiff(LogRecordProperties logRecordProperties) {
        DIFF_MSG_FORMAT = logRecordProperties.getDiffMsgFormat().equals(DEFAULT_DIFF_MSG_FORMAT) ? DEFAULT_DIFF_MSG_FORMAT : new String(logRecordProperties.getDiffMsgFormat().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        DIFF_MSG_SEPARATOR = logRecordProperties.getDiffMsgSeparator().equals(DEFAULT_DIFF_MSG_SEPARATOR) ? DEFAULT_DIFF_MSG_SEPARATOR : new String(logRecordProperties.getDiffMsgSeparator().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        diffIgnoreOldObjectNullValue = logRecordProperties.getDiffIgnoreOldObjectNullValue();
        diffIgnoreNewObjectNullValue = logRecordProperties.getDiffIgnoreNewObjectNullValue();
        log.info("CustomFunctionObjectDiff init diffMsgFormat [{}] diffMsgSeparator [{}]", DIFF_MSG_FORMAT, DIFF_MSG_SEPARATOR);
        log.info("CustomFunctionObjectDiff init diffIgnoreOldObjectNullValue [{}] diffIgnoreNewObjectNullValue [{}]", diffIgnoreOldObjectNullValue, diffIgnoreNewObjectNullValue);
    }

    /**
     * 默认的DIFF方法实现
     *
     * @return DIFF日志消息文案
     */
    @LogRecordFunc("_DIFF")
    public static String objectDiff(Object oldObject, Object newObject) throws LogRecordException {
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
        boolean newClassEnableAllFields = newObjectLogRecordDiff != null && newObjectLogRecordDiff.enableAllFields();
        log.debug("oldClassEnableAllFields [{}] newClassEnableAllFields [{}]", oldClassEnableAllFields, newClassEnableAllFields);

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
        Field[] oldObjectFields = getAllFields(oldObject.getClass());
        for (Field oldField : oldObjectFields) {
            try {
                // 获取老字段值
                oldField.setAccessible(true);
                Object oldValue = oldField.get(oldObject);

                LogRecordDiffField oldFieldLogRecordDiffField = oldField.getDeclaredAnnotation(LogRecordDiffField.class);
                // 根据老字段判断是否需要进行diff
                if (!judgeFieldDiffNeeded(oldValue, false, oldClassEnableAllFields, oldFieldLogRecordDiffField)) {
                    log.debug("oldField [{}] not need to diff, skip", oldField.getName());
                    continue;
                }
                // 在新字段中寻找同名字段，若找不到则抛出NoSuchFieldException异常跳过本次遍历
                Field newField = getFieldByName(newObject.getClass(), oldField.getName());
                if (newField == null) {
                    log.info("no field named [{}] in newObject, skip", oldField.getName());
                    continue;
                }
                LogRecordDiffField newFieldLogRecordDiffField = newField.getDeclaredAnnotation(LogRecordDiffField.class);
                // 获取新字段值
                newField.setAccessible(true);
                Object newValue = newField.get(newObject);
                // 根据新字段判断是否需要进行diff
                if (!judgeFieldDiffNeeded(newValue, true, newClassEnableAllFields, newFieldLogRecordDiffField)) {
                    log.debug("newField [{}] not need to diff, skip", newField.getName());
                    continue;
                }

                // 通过LogRecordDiffField获取字段别名
                if (oldFieldLogRecordDiffField != null && newFieldLogRecordDiffField != null) {
                    String oldFieldAlias = StringUtils.isNotBlank(oldFieldLogRecordDiffField.alias()) ? oldFieldLogRecordDiffField.alias() : null;
                    String newFieldAlias = StringUtils.isNotBlank(newFieldLogRecordDiffField.alias()) ? newFieldLogRecordDiffField.alias() : null;
                    oldFieldAliasMap.put(oldField.getName(), oldFieldAlias);
                    newFieldAliasMap.put(newField.getName(), newFieldAlias);
                    log.debug("field [{}] has annotation oldField alias [{}] newField alias [{}]", oldField.getName(), oldFieldAlias, newFieldAlias);
                }

                // 对比新老字段值
                if (!fieldValueEquals(oldValue, newValue)) {
                    log.debug("field [{}] is different between oldObject [{}] newObject [{}]", oldField.getName(), oldValue, newValue);
                    oldValueMap.put(oldField.getName(), oldValue);
                    newValueMap.put(newField.getName(), newValue);
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
        diffDTO.setDiffFieldDTOList(diffFieldDTOList);
        for (Map.Entry<String, Object> entry : oldValueMap.entrySet()) {
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
            valuesMap.put("_oldValue", oldValue == null ? DEFAULT_DIFF_NULL_TEXT : oldValue.toString());
            valuesMap.put("_newValue", newValue == null ? DEFAULT_DIFF_NULL_TEXT : newValue.toString());
            StringSubstitutor sub = new StringSubstitutor(valuesMap);
            diffMsgList.add(sub.replace(DIFF_MSG_FORMAT));
        }
        msg.append(String.join(DIFF_MSG_SEPARATOR, diffMsgList));
        LogRecordContext.addDiffDTO(diffDTO);
        return msg.toString();
    }

    /**
     * 判断field是否需要进行DIFF
     * 规则如下：
     * 1. 类开启EnableAllFields并且字段未开启ignored 或 字段开启LogRecordDiffField并且字段未开启ignored
     * 2. 全局配置忽略值为null的字段
     */
    private static boolean judgeFieldDiffNeeded(Object objectValue, boolean isNewObject, boolean classEnableAllFields, LogRecordDiffField fieldLogRecordDiffField) {
        boolean annotationChecker1 = classEnableAllFields && (fieldLogRecordDiffField == null || !fieldLogRecordDiffField.ignored());
        boolean annotationChecker2 = fieldLogRecordDiffField != null && !fieldLogRecordDiffField.ignored();
        boolean ignoreNullValue = objectValue == null && ((isNewObject && diffIgnoreNewObjectNullValue) || (!isNewObject && diffIgnoreOldObjectNullValue));
        return (annotationChecker1 || annotationChecker2) && !ignoreNullValue;
    }

    /**
     * 判断新旧字段值是否相同
     */
    private static boolean fieldValueEquals(Object oldValue, Object newValue) throws LogRecordException {
        try {
            // 全为null返回相同
            if (oldValue == null && newValue == null) {
                return true;
            }
            // 有一个为null返回不相同
            if (oldValue == null || newValue == null) {
                return false;
            }
            // 全为非null
            boolean isAllPrimitive = isWrapClassOrPrimitive(oldValue.getClass()) && isWrapClassOrPrimitive(newValue.getClass());
            boolean isAllNotPrimitive = !isWrapClassOrPrimitive(oldValue.getClass()) && !isWrapClassOrPrimitive(newValue.getClass());
            // 若为基本类型
            // 旧值为空并且新值不为空
            // 或
            // 旧值不为空且新值为空
            // 或
            // 旧值和新值均不为空且equals不相等
            if (isAllPrimitive) {
                return oldValue.equals(newValue);
            }

            // 若为非基本类型：转化为JSONObject或者JSONArray进行比较
            else if (isAllNotPrimitive) {
                if (isJsonArray(oldValue) && isJsonArray(newValue)) {
                    JSONArray oldJsonArray = (JSONArray) JSONArray.toJSON(oldValue);
                    JSONArray newJsonArray = (JSONArray) JSONArray.toJSON(newValue);
                    return oldJsonArray.equals(newJsonArray);
                } else if (!isJsonArray(oldValue.getClass()) && !isJsonArray(newValue.getClass())) {
                    // 尝试转化为JSONObject进行比较，若强转失败则使用equals，依赖于类的equals实现
                    try {
                        JSONObject oldJsonObject = (JSONObject) JSONObject.toJSON(oldValue);
                        JSONObject newJsonObject = (JSONObject) JSONObject.toJSON(newValue);
                        return oldJsonObject.equals(newJsonObject);
                    } catch (ClassCastException e) {
                        return oldValue.equals(newValue);
                    }
                } else {
                    return false;
                }
            }

            // 若一个基本类型一个非基本类型返回不相等
            else {
                return false;
            }
        } catch (Exception e) {
            throw new LogRecordException("fieldValueEquals error", e);
        }
    }

    /**
     * 是否为基础类型或者其包装类
     */
    private static boolean isWrapClassOrPrimitive(Class clz) {
        return clz.isPrimitive() || clz == Integer.class || clz == Long.class || clz == Short.class
                || clz == Boolean.class || clz == Byte.class || clz == Float.class || clz == Double.class
                || clz == String.class;
    }

    /**
     * 是否为数组类型（可解析为JSONArray）
     */
    private static boolean isJsonArray(Object obj) {
        return obj.getClass().isArray() || obj instanceof Collection;
    }

    /**
     * 获取类所有字段 循环遍历所有父类
     */
    private static Field[] getAllFields(Class<?> type) {
        List<Field> fields = new ArrayList<>();
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            Collections.addAll(fields, c.getDeclaredFields());
        }
        return fields.toArray(new Field[0]);
    }

    /**
     * 获取类指定字段
     * 使用了一个设计为通过异常来指示特定条件的Java标准库方法，这确实是一个特殊情况，通常不会使用异常来做业务逻辑。
     */
    private static Field getFieldByName(Class<?> type, String fieldName) {
        for (Class<?> c = type; c != null; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
            }
        }
        return null;
    }

}

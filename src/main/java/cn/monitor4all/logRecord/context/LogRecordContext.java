package cn.monitor4all.logRecord.context;

import cn.monitor4all.logRecord.bean.DiffDTO;
import org.springframework.core.NamedThreadLocal;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.List;

public class LogRecordContext {

    private static final ThreadLocal<StandardEvaluationContext> CONTEXT_THREAD_LOCAL = new NamedThreadLocal<>("ThreadLocal StandardEvaluationContext");

    public static StandardEvaluationContext getContext() {
        return CONTEXT_THREAD_LOCAL.get() == null ? new StandardEvaluationContext(): CONTEXT_THREAD_LOCAL.get();
    }

    public static void putVariables(String key, Object value) {
        StandardEvaluationContext context = getContext();
        context.setVariable(key, value);
        CONTEXT_THREAD_LOCAL.set(context);
    }

    public static void clearContext() {
        CONTEXT_THREAD_LOCAL.remove();
    }


    private static final ThreadLocal<List<DiffDTO>> DIFF_DTO_LIST_THREAD_LOCAL = new NamedThreadLocal<>("ThreadLocal DiffDTOList");

    public static List<DiffDTO> getDiffDTOList() {
        return DIFF_DTO_LIST_THREAD_LOCAL.get() == null ? new ArrayList<>() : DIFF_DTO_LIST_THREAD_LOCAL.get();
    }

    public static void addDiffDTO(DiffDTO diffDTO) {
        if (diffDTO != null) {
            List<DiffDTO> diffDTOList = getDiffDTOList();
            diffDTOList.add(diffDTO);
            DIFF_DTO_LIST_THREAD_LOCAL.set(diffDTOList);
        }
    }

    public static void clearDiffDTOList() {
        DIFF_DTO_LIST_THREAD_LOCAL.remove();
    }


}

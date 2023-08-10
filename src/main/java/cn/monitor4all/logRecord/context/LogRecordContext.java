package cn.monitor4all.logRecord.context;

import cn.monitor4all.logRecord.bean.DiffDTO;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 上下文
 */
public class LogRecordContext {

    private static final TransmittableThreadLocal<StandardEvaluationContext> CONTEXT_THREAD_LOCAL = new TransmittableThreadLocal<>();

    public static final String CONTEXT_KEY_NAME_RETURN = "_return";

    public static final String CONTEXT_KEY_NAME_ERROR_MSG = "_errorMsg";

    public static final String CONTEXT_KEY_NAME_REQUEST = "_request";

    public static StandardEvaluationContext getContext() {
        return CONTEXT_THREAD_LOCAL.get() == null ? new StandardEvaluationContext(): CONTEXT_THREAD_LOCAL.get();
    }

    public static void putVariable(String key, Object value) {
        StandardEvaluationContext context = getContext();
        context.setVariable(key, value);
        CONTEXT_THREAD_LOCAL.set(context);
    }

    public static Object getVariable(String key) {
        StandardEvaluationContext context = getContext();
        return context.lookupVariable(key);
    }

    public static void clearContext() {
        CONTEXT_THREAD_LOCAL.remove();
    }


    private static final TransmittableThreadLocal<List<DiffDTO>> DIFF_DTO_LIST_THREAD_LOCAL = new TransmittableThreadLocal<>();

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

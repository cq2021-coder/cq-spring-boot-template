package com.cq.template.utils;

import com.cq.template.common.ResultCodeEnum;
import com.cq.template.exception.BusinessException;

/**
 * 抛异常工具类
 *
 * @author cq
 * @since 2024/03/25
 */
public class ThrowUtils {

    /**
     * 条件成立则抛异常
     *
     * @param condition        条件
     * @param runtimeException 运行时异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition      条件
     * @param resultCodeEnum 错误代码
     */
    public static void throwIf(boolean condition, ResultCodeEnum resultCodeEnum) {
        throwIf(condition, new BusinessException(resultCodeEnum));
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition      条件
     * @param resultCodeEnum 错误代码
     * @param message        消息
     */
    public static void throwIf(boolean condition, ResultCodeEnum resultCodeEnum, String message) {
        throwIf(condition, new BusinessException(resultCodeEnum, message));
    }
}

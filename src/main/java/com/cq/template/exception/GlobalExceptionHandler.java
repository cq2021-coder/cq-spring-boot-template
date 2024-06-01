package com.cq.template.exception;

import com.cq.template.common.CommonResult;
import com.cq.template.common.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author 程崎
 * @since 2023/07/29
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public CommonResult<?> businessExceptionHandler(BusinessException e) {
        log.info("BusinessException", e);
        if (e.getMessage() != null) {
            return CommonResult.error(e.getResultCodeEnum(), e.getMessage());
        }
        return CommonResult.error(e.getResultCodeEnum());
    }

    @ExceptionHandler(BindException.class)
    public CommonResult<?> validExceptionHandler(BindException e) {
        String errorMessage = e.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        log.warn("parameter validation failed:{}", errorMessage);
        return CommonResult.error(ResultCodeEnum.PARAMS_ERROR, errorMessage);
    }


    @ExceptionHandler(RuntimeException.class)
    public CommonResult<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return CommonResult.error(ResultCodeEnum.SYSTEM_ERROR);
    }
}

package com.cq.template.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用返回类
 *
 * @author 程崎
 * @since 2023/10/10
 */
@Data
public class CommonResult<T> implements Serializable {
    private int code;
    private T data;
    private String message;

    private CommonResult(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    private CommonResult() {
    }

    public static <T> CommonResult<T> success() {
        return success(null);
    }

    public static <T> CommonResult<T> success(T data) {
        return success(data, "ok");
    }

    public static <T> CommonResult<T> error(T data) {
        return error(data, "ok");
    }

    public static <T> CommonResult<T> success(T data, String message) {
        return build(200, data, message);
    }

    public static <T> CommonResult<T> error(T data, String message) {
        return build(-1, data, message);
    }

    public static <T> CommonResult<T> error(ResultCodeEnum resultCodeEnum) {
        return build(resultCodeEnum.getCode(), null, resultCodeEnum.getMessage());
    }

    public static <T> CommonResult<T> error(ResultCodeEnum resultCodeEnum, String msg) {
        return build(resultCodeEnum.getCode(), null, msg);
    }

    public static <T> CommonResult<T> build(int code, T data, String message) {
        return new CommonResult<>(code, data, message);
    }

    @Serial
    private static final long serialVersionUID = 1L;
}

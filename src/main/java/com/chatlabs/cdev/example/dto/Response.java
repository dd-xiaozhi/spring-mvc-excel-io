package com.chatlabs.cdev.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应包装类
 * 演示如何使用 ResponseWrapper 解封装响应
 *
 * @author DD
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {

    private int code;
    private String message;
    private T data;

    public static <T> Response<T> success() {
        return new Response<>(200, "success", null);
    }

    public static <T> Response<T> success(T data) {
        return new Response<>(200, "success", data);
    }

    public static <T> Response<T> error(String message) {
        return new Response<>(500, message, null);
    }
}

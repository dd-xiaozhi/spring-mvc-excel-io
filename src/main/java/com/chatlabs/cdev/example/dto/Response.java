package com.chatlabs.cdev.example.dto;

import lombok.Data;

/**
 * 通用响应包装类
 * 
 * @author DD
 */
@Data
public class Response<T> {
    
    private Integer code;
    private String message;
    private T data;
    
    public Response() {
    }
    
    public Response(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "success", data);
    }
    
    public static <T> Response<T> error(String message) {
        return new Response<>(500, message, null);
    }
}

package com.chatlabs.cdev.example.wrapper;

import com.chatlabs.cdev.example.dto.Response;
import com.chatlabs.cdev.wrapper.ResponseWrapper;
import org.springframework.stereotype.Component;

/**
 * 示例响应包装器
 * 用于处理 Response 类型的响应，自动解封装提取 data 字段
 * 
 * @author DD
 */
@Component
public class ExampleResponseWrapper implements ResponseWrapper {
    
    @Override
    public boolean supports(Class<?> clazz) {
        return Response.class.isAssignableFrom(clazz);
    }
    
    @Override
    public Object unwrap(Object result) {
        if (result instanceof Response<?> response) {
            return response.getData();
        }
        return result;
    }
    
    @Override
    public Object wrap(Object data) {
        return Response.success();
    }
    
    @Override
    public int getOrder() {
        return 100;
    }
}

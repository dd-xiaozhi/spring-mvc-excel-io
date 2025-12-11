package com.chatlabs.cdev.example.wrapper;

import com.chatlabs.cdev.example.dto.Response;
import com.chatlabs.cdev.wrapper.ResponseWrapper;
import org.springframework.stereotype.Component;

/**
 * Response 响应包装器实现
 * 演示如何封装和解封装自定义响应类
 * 
 * @author DD
 */
@Component
public class ResponseWrapperImpl implements ResponseWrapper {
    
    @Override
    public boolean supports(Object result) {
        return result instanceof Response;
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
        return Response.success(data);
    }
    
    @Override
    public int getOrder() {
        return 100; // 优先级高于默认包装器
    }
}


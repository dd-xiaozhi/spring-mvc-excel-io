package com.chatlabs.cdev.wrapper;

import org.springframework.stereotype.Component;

/**
 * 默认响应包装器
 * 不做任何处理，直接返回原始数据
 * 
 * @author DD
 */
@Component
public class DefaultResponseWrapper implements ResponseWrapper {
    
    @Override
    public boolean supports(Object result) {
        // 最低优先级，兜底处理
        return true;
    }
    
    @Override
    public Object unwrap(Object result) {
        return result;
    }
    
    @Override
    public Object wrap(Object data) {
        return data;
    }
    
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}


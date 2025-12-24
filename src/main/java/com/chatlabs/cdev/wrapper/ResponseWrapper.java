package com.chatlabs.cdev.wrapper;

/**
 * 响应包装器接口
 * 负责封装和解封装响应数据
 * 
 * @author DD
 */
public interface ResponseWrapper {

    /**
     * 判断包装器是否支持指定类
     *
     * @param clazz 类
     * @return 是否支持
     */
    boolean supports(Class<?> clazz);
    
    /**
     * 从包装对象中提取实际数据（解封装）
     * 
     * @param result 包装对象
     * @return 实际数据
     */
    Object unwrap(Object result);
    
    /**
     * 将数据包装成响应对象（封装）
     * 
     * @param data 实际数据
     * @return 包装后的响应对象
     */
    Object wrap(Object data);
    
    /**
     * 获取优先级，数值越小优先级越高
     * 
     * @return 优先级
     */
    default int getOrder() {
        return Integer.MAX_VALUE;
    }
}


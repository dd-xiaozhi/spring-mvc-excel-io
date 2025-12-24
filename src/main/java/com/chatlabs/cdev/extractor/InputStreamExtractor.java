package com.chatlabs.cdev.extractor;

import jakarta.servlet.http.HttpServletRequest;

import java.io.InputStream;

/**
 * 输入流提取器接口
 * 用于从请求中提取Excel文件的输入流
 * 
 * @author DD
 */
public interface InputStreamExtractor {
    
    /**
     * 判断是否支持该请求
     * 
     * @param request HTTP请求
     * @param fieldName 字段名
     * @return 是否支持
     */
    boolean supports(HttpServletRequest request, String fieldName);
    
    /**
     * 从请求中提取输入流
     * 
     * @param request HTTP请求
     * @param fieldName 字段名
     * @return 输入流，如果无法提取则返回null
     * @throws Exception 提取异常
     */
    InputStream extract(HttpServletRequest request, String fieldName) throws Exception;
    
    /**
     * 获取优先级，数值越小优先级越高
     * 
     * @return 优先级
     */
    default int getOrder() {
        return Integer.MAX_VALUE;
    }
}

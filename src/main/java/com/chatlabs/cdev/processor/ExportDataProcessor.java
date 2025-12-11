package com.chatlabs.cdev.processor;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 导出数据处理器接口
 * 用于扩展 Excel 导出的处理逻辑
 * 
 * @author DD
 */
public interface ExportDataProcessor {
    
    /**
     * 处理导出数据
     * 
     * @param data 要导出的数据列表
     * @param dataClass 数据类型
     * @param fileName 文件名
     * @param sheetName Sheet 名称
     * @param response HTTP 响应对象
     * @throws Exception 处理异常
     */
    void process(List<?> data, Class<?> dataClass, String fileName, 
                 String sheetName, HttpServletResponse response) throws Exception;
}


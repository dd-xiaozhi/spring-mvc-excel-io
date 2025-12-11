package com.chatlabs.cdev.reader;

import java.io.InputStream;
import java.util.List;

/**
 * Excel 读取器接口
 * 用于扩展 Excel 读取逻辑
 * 
 * @author DD
 */
public interface ExcelReader {
    
    /**
     * 读取 Excel 数据
     * 
     * @param inputStream Excel 文件输入流
     * @param dataClass 目标数据类型
     * @param <T> 数据类型泛型
     * @return 数据列表
     * @throws Exception 读取异常
     */
    <T> List<T> read(InputStream inputStream, Class<T> dataClass) throws Exception;
}


package com.chatlabs.cdev.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.chatlabs.cdev.exception.ExcelReadException;
import com.chatlabs.cdev.exception.ExcelWriteException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Excel 工具类
 * 提供便捷的 Excel 操作方法
 * 
 * @author DD
 */
public class ExcelUtils {
    
    private ExcelUtils() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    /**
     * 写入 Excel 到输出流
     * 
     * @param outputStream 输出流
     * @param dataClass 数据类型
     * @param data 数据列表
     * @param sheetName Sheet 名称
     */
    public static void write(OutputStream outputStream, Class<?> dataClass, 
                           List<?> data, String sheetName) {
        try {
            EasyExcel.write(outputStream, dataClass)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(sheetName)
                    .doWrite(data);
        } catch (Exception e) {
            throw new ExcelWriteException("写入 Excel 失败", e);
        }
    }
    
    /**
     * 从输入流读取 Excel
     * 
     * @param inputStream 输入流
     * @param dataClass 数据类型
     * @param <T> 数据类型泛型
     * @return 数据列表
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> dataClass) {
        try {
            return EasyExcel.read(inputStream)
                    .head(dataClass)
                    .sheet()
                    .doReadSync();
        } catch (Exception e) {
            throw new ExcelReadException("读取 Excel 失败", e);
        }
    }
    
    /**
     * 从输入流读取 Excel（指定 Sheet）
     * 
     * @param inputStream 输入流
     * @param dataClass 数据类型
     * @param sheetNo Sheet 索引（从 0 开始）
     * @param <T> 数据类型泛型
     * @return 数据列表
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> dataClass, int sheetNo) {
        try {
            return EasyExcel.read(inputStream)
                    .head(dataClass)
                    .sheet(sheetNo)
                    .doReadSync();
        } catch (Exception e) {
            throw new ExcelReadException("读取 Excel 失败: Sheet " + sheetNo, e);
        }
    }
}


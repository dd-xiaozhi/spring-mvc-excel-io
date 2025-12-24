package com.chatlabs.cdev.reader;

import com.alibaba.excel.EasyExcel;
import com.chatlabs.cdev.exception.ExcelReadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * 默认Excel读取器
 * 
 * @author DD
 */
@Slf4j
@Component
public class DefaultExcelReader implements ExcelReader {
    
    @Override
    public <T> List<T> read(InputStream inputStream, Class<T> dataClass) {
        if (inputStream == null) {
            throw new ExcelReadException("输入流不能为空");
        }
        
        long start = System.currentTimeMillis();
        try {
            List<T> data = EasyExcel.read(inputStream).head(dataClass).sheet().doReadSync();
            log.info("读取完成: {}, {}条, {}ms", dataClass.getSimpleName(), data.size(), System.currentTimeMillis() - start);
            return data;
        } catch (Exception e) {
            log.error("读取失败: {}", dataClass.getSimpleName(), e);
            throw new ExcelReadException("读取失败: " + dataClass.getSimpleName(), e);
        }
    }
}


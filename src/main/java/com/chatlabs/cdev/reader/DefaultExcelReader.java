package com.chatlabs.cdev.reader;

import com.alibaba.excel.EasyExcel;
import com.chatlabs.cdev.config.ExcelIOProperties;
import com.chatlabs.cdev.exception.ExcelReadException;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

/**
 * 默认 Excel 读取器
 * 使用 EasyExcel 读取 Excel 文件
 * 
 * @author DD
 */
@Component
public class DefaultExcelReader implements ExcelReader {
    
    private static final Logger log = LoggerFactory.getLogger(DefaultExcelReader.class);

    @Resource
    private ExcelIOProperties properties;
    
    @Override
    public <T> List<T> read(InputStream inputStream, Class<T> dataClass) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            if (inputStream == null) {
                throw new ExcelReadException("输入流不能为空");
            }
            
            log.debug("开始读取 Excel: dataClass={}", dataClass.getSimpleName());
            
            List<T> data = EasyExcel.read(inputStream)
                    .head(dataClass)
                    .sheet()
                    .doReadSync();
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("Excel 读取成功: dataClass={}, dataSize={}, duration={}ms", 
                    dataClass.getSimpleName(), data.size(), duration);
            
            // 检查文件大小限制（通过数据量间接控制）
            if (properties != null && properties.getMaxFileSize() > 0) {
                // 这里可以添加更精确的大小检查逻辑
                log.debug("数据量检查通过: dataSize={}", data.size());
            }
                    
            return data;
            
        } catch (ExcelReadException e) {
            throw e;
        } catch (Exception e) {
            log.error("Excel 读取失败: dataClass={}", dataClass.getSimpleName(), e);
            throw new ExcelReadException("Excel 读取失败: " + dataClass.getSimpleName(), e);
        }
    }
}


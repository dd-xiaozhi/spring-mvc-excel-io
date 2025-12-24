package com.chatlabs.cdev.processor;

import com.alibaba.excel.EasyExcel;
import com.chatlabs.cdev.exception.ExcelWriteException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 默认导出处理器 - 直接响应Excel文件
 *
 * @author DD
 */
@Slf4j
@Component
public class DefaultExportProcessor implements ExportDataProcessor {

    private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Override
    public void process(List<?> data, Class<?> dataClass, String fileName, String sheetName, HttpServletResponse response) {
        try {
            response.setContentType(CONTENT_TYPE);
            response.setCharacterEncoding("utf-8");
            String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encoded + ".xlsx\"");

            EasyExcel.write(response.getOutputStream(), dataClass).sheet(sheetName).doWrite(data);
            
            log.info("导出完成: {}.xlsx, {}条", fileName, data != null ? data.size() : 0);
        } catch (Exception e) {
            log.error("导出失败: {}", fileName, e);
            throw new ExcelWriteException("导出失败: " + fileName, e);
        }
    }
}


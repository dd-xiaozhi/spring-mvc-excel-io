package com.chatlabs.cdev.processor;

import com.alibaba.excel.EasyExcel;
import com.chatlabs.cdev.exception.ExcelWriteException;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 默认导出处理器
 * 直接将 Excel 文件作为附件响应给前端
 *
 * @author DD
 */
@Component
public class DefaultExportProcessor implements ExportDataProcessor {

    private static final Logger log = LoggerFactory.getLogger(DefaultExportProcessor.class);

    @Override
    public void process(List<?> data, Class<?> dataClass, String fileName, String sheetName, HttpServletResponse response) {

        long startTime = System.currentTimeMillis();

        try {
            // 数据量检查
            if (data == null || data.isEmpty()) {
                log.warn("导出数据为空: fileName={}", fileName);
            }

            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");

            // 文件名编码，防止中文乱码
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + encodedFileName + ".xlsx\"");

            // 使用 EasyExcel 写入数据
            EasyExcel.write(response.getOutputStream(), dataClass)
                    .sheet(sheetName)
                    .doWrite(data);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Excel 导出成功: fileName={}, sheetName={}, dataSize={}, duration={}ms",
                    fileName, sheetName, data != null ? data.size() : 0, duration);

        } catch (IOException e) {
            log.error("Excel 导出失败: fileName={}, dataClass={}", fileName, dataClass.getSimpleName(), e);
            throw new ExcelWriteException("Excel 导出失败: " + fileName, e);
        } catch (Exception e) {
            log.error("Excel 导出过程发生未知错误: fileName={}", fileName, e);
            throw new ExcelWriteException("Excel 导出过程发生错误", e);
        }
    }
}


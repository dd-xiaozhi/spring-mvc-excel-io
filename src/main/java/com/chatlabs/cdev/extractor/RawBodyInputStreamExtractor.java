package com.chatlabs.cdev.extractor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * 原始请求体输入流提取器
 * 支持直接将Excel文件作为请求体发送（二进制上传）
 * 
 * Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
 * 或
 * Content-Type: application/octet-stream
 * 
 * @author DD
 */
@Slf4j
@Component
public class RawBodyInputStreamExtractor implements InputStreamExtractor {
    
    private static final String EXCEL_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String OCTET_STREAM = "application/octet-stream";
    private static final String XLS_CONTENT_TYPE = "application/vnd.ms-excel";
    
    @Override
    public boolean supports(HttpServletRequest request, String fieldName) {
        String contentType = request.getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.contains(EXCEL_CONTENT_TYPE) 
                || contentType.contains(OCTET_STREAM)
                || contentType.contains(XLS_CONTENT_TYPE);
    }
    
    @Override
    public InputStream extract(HttpServletRequest request, String fieldName) throws Exception {
        if (request.getContentLength() <= 0) {
            log.debug("请求体为空");
            return null;
        }
        
        log.debug("提取原始请求体: contentLength={}", request.getContentLength());
        return request.getInputStream();
    }
    
    @Override
    public int getOrder() {
        return 300;
    }
}

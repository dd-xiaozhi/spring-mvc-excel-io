package com.chatlabs.cdev.extractor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.InputStream;

/**
 * Multipart表单输入流提取器
 * 支持 multipart/form-data 方式上传
 * 
 * @author DD
 */
@Slf4j
@Component
public class MultipartInputStreamExtractor implements InputStreamExtractor {
    
    @Override
    public boolean supports(HttpServletRequest request, String fieldName) {
        return request instanceof MultipartHttpServletRequest;
    }
    
    @Override
    public InputStream extract(HttpServletRequest request, String fieldName) throws Exception {
        if (!(request instanceof MultipartHttpServletRequest multipartRequest)) {
            return null;
        }
        
        MultipartFile file = multipartRequest.getFile(fieldName);
        if (file == null || file.isEmpty()) {
            log.debug("Multipart文件为空: fieldName={}", fieldName);
            return null;
        }
        
        log.debug("提取Multipart文件: fieldName={}, size={}", fieldName, file.getSize());
        return file.getInputStream();
    }
    
    @Override
    public int getOrder() {
        return 100;
    }
}

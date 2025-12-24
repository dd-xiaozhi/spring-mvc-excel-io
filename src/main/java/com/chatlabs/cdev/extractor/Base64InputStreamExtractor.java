package com.chatlabs.cdev.extractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

/**
 * Base64编码输入流提取器
 * 支持JSON请求体中包含Base64编码的文件数据
 * 
 * 请求格式示例:
 * {
 *   "file": "base64编码的文件内容"
 * }
 * 
 * @author DD
 */
@Slf4j
@Component
public class Base64InputStreamExtractor implements InputStreamExtractor {
    
    private static final String APPLICATION_JSON = "application/json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean supports(HttpServletRequest request, String fieldName) {
        String contentType = request.getContentType();
        return contentType != null && contentType.contains(APPLICATION_JSON);
    }
    
    @Override
    public InputStream extract(HttpServletRequest request, String fieldName) {
        String contentType = request.getContentType();
        if (contentType == null || !contentType.contains(APPLICATION_JSON)) {
            return null;
        }
        
        try {
            JsonNode root = objectMapper.readTree(request.getInputStream());
            JsonNode fieldNode = root.get(fieldName);
            
            if (fieldNode == null || fieldNode.isNull() || !fieldNode.isTextual()) {
                log.debug("JSON中未找到Base64字段: fieldName={}", fieldName);
                return null;
            }
            
            String base64Content = fieldNode.asText();
            // 移除可能的data:前缀 (如 data:application/vnd.ms-excel;base64,)
            if (base64Content.contains(",")) {
                base64Content = base64Content.substring(base64Content.indexOf(",") + 1);
            }
            
            byte[] decoded = Base64.getDecoder().decode(base64Content);
            log.debug("提取Base64文件: fieldName={}, size={}", fieldName, decoded.length);
            return new ByteArrayInputStream(decoded);
        } catch (Exception e) {
            log.debug("Base64解析失败: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public int getOrder() {
        return 200;
    }
}

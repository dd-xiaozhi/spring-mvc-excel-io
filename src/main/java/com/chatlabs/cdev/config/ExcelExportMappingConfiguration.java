package com.chatlabs.cdev.config;

import com.chatlabs.cdev.annotation.ExcelExport;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Excel 导出映射配置
 * 通过 Spring MVC 标准扩展机制注册导出路径
 * 
 * @author DD
 */
@Slf4j
@Component
public class ExcelExportMappingConfiguration implements InitializingBean {
    
    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Override
    public void afterPropertiesSet() {
        registerExportMappings();
    }
    
    /**
     * 注册所有导出映射
     */
    private void registerExportMappings() {
        // 获取所有已注册的映射
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = 
                requestMappingHandlerMapping.getHandlerMethods();
        
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            Method method = handlerMethod.getMethod();
            
            // 检查是否有 @ExcelExport 注解
            ExcelExport excelExport = AnnotatedElementUtils.findMergedAnnotation(method, ExcelExport.class);
            if (excelExport == null) {
                continue;
            }
            
            try {
                registerExportMapping(entry.getKey(), handlerMethod, excelExport);
            } catch (Exception e) {
                log.error("注册导出映射失败: method={}", method.getName(), e);
            }
        }
    }
    
    /**
     * 注册单个导出映射
     */
    private void registerExportMapping(RequestMappingInfo originalMapping, 
                                       HandlerMethod handlerMethod,
                                       ExcelExport excelExport) {
        
        // 获取原始路径
        String originalPath = getOriginalPath(originalMapping);
        if (originalPath == null) {
            log.warn("无法获取原始路径，跳过导出映射注册: method={}", 
                    handlerMethod.getMethod().getName());
            return;
        }
        
        // 确定导出路径
        String exportPath = determineExportPath(originalPath, excelExport.value());
        
        // 创建导出映射信息
        RequestMappingInfo exportMappingInfo = RequestMappingInfo
                .paths(exportPath)
                .methods(RequestMethod.GET)
                .params(originalMapping.getParamsCondition().getExpressions().toArray(new String[0]))
                .headers(originalMapping.getHeadersCondition().getExpressions().toArray(new String[0]))
                .consumes(originalMapping.getConsumesCondition().getExpressions().toArray(new String[0]))
                .produces(originalMapping.getProducesCondition().getExpressions().toArray(new String[0]))
                .build();
        
        // 注册映射
        requestMappingHandlerMapping.registerMapping(
                exportMappingInfo, 
                handlerMethod.getBean(), 
                handlerMethod.getMethod()
        );
        
        log.info("注册导出映射成功: 原路径={}, 导出路径={}, method={}", 
                originalPath, exportPath, handlerMethod.getMethod().getName());
    }
    
    /**
     * 从 RequestMappingInfo 获取路径
     */
    private String getOriginalPath(RequestMappingInfo mappingInfo) {
        if (mappingInfo.getPathPatternsCondition() != null && 
            !mappingInfo.getPathPatternsCondition().getPatterns().isEmpty()) {
            return mappingInfo.getPathPatternsCondition().getPatterns()
                    .iterator().next().getPatternString();
        }
        
        if (mappingInfo.getPatternsCondition() != null && 
            !mappingInfo.getPatternsCondition().getPatterns().isEmpty()) {
            return mappingInfo.getPatternsCondition().getPatterns()
                    .iterator().next();
        }
        
        return null;
    }
    
    /**
     * 确定导出路径
     */
    private String determineExportPath(String originalPath, String customPath) {
        // 如果指定了自定义路径，直接使用
        if (StringUtils.hasText(customPath)) {
            return customPath;
        }
        
        // 默认在原路径后加 /export
        if (originalPath.endsWith("/")) {
            return originalPath + "export";
        } else {
            return originalPath + "/export";
        }
    }
}


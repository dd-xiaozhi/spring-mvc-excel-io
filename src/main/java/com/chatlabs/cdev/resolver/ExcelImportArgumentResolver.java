package com.chatlabs.cdev.resolver;

import com.chatlabs.cdev.annotation.ExcelImport;
import com.chatlabs.cdev.config.ExcelIOProperties;
import com.chatlabs.cdev.exception.ExcelIOException;
import com.chatlabs.cdev.exception.ExcelParseException;
import com.chatlabs.cdev.reader.ExcelReader;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.InputStream;
import java.util.List;

/**
 * Excel 导入参数解析器
 * 将上传的 Excel 文件自动转换为对象列表
 * 
 * @author DD
 */
@Component
public class ExcelImportArgumentResolver implements HandlerMethodArgumentResolver {
    
    private static final Logger log = LoggerFactory.getLogger(ExcelImportArgumentResolver.class);
    
    private final ApplicationContext applicationContext;
    private final ExcelIOProperties properties;
    
    public ExcelImportArgumentResolver(ApplicationContext applicationContext, ExcelIOProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }
    
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ExcelImport.class);
    }
    
    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        
        ExcelImport annotation = parameter.getParameterAnnotation(ExcelImport.class);
        if (annotation == null) {
            return null;
        }
        
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            throw new ExcelIOException("无法获取 HttpServletRequest");
        }
        
        try {
            log.debug("开始解析 Excel 参数: fieldName={}, dataClass={}", 
                    annotation.value(), annotation.dataClass().getSimpleName());
            
            // 获取输入流
            InputStream inputStream = getInputStream(request, annotation, parameter);
            
            if (inputStream == null) {
                if (annotation.required()) {
                    throw new ExcelParseException("未找到上传文件: " + annotation.value());
                }
                log.debug("文件不存在且非必需，返回 null");
                return null;
            }
            
            // 检查文件大小
            if (properties != null && request.getContentLength() > properties.getMaxFileSize()) {
                throw new ExcelParseException(String.format(
                        "上传文件大小超出限制: %d bytes > %d bytes", 
                        request.getContentLength(), properties.getMaxFileSize()));
            }
            
            // 根据参数类型处理
            Class<?> paramType = parameter.getParameterType();
            
            // 如果参数就是 MultipartFile/InputStream/byte[]，直接返回
            if (MultipartFile.class.isAssignableFrom(paramType)) {
                return getMultipartFile(request, annotation.value());
            }
            
            if (InputStream.class.isAssignableFrom(paramType)) {
                return inputStream;
            }
            
            if (byte[].class.isAssignableFrom(paramType)) {
                return inputStream.readAllBytes();
            }
            
            // 否则，解析为对象列表
            ExcelReader reader = getReader(annotation);
            List<?> result = reader.read(inputStream, annotation.dataClass());
            
            log.debug("Excel 参数解析完成: dataSize={}", result.size());
            return result;
            
        } catch (ExcelIOException e) {
            throw e;
        } catch (Exception e) {
            log.error("Excel 参数解析失败: paramType={}, annotationValue={}", 
                    parameter.getParameterType().getSimpleName(), annotation.value(), e);
            throw new ExcelParseException("Excel 参数解析失败", e);
        }
    }
    
    /**
     * 获取输入流
     */
    private InputStream getInputStream(HttpServletRequest request, 
                                       ExcelImport annotation,
                                       MethodParameter parameter) throws Exception {
        
        // 支持 multipart/form-data
        if (request instanceof MultipartHttpServletRequest multipartRequest) {
            MultipartFile file = multipartRequest.getFile(annotation.value());
            if (file != null && !file.isEmpty()) {
                return file.getInputStream();
            }
        }
        
        // 支持直接从请求体读取（如二进制上传）
        if (request.getContentLength() > 0) {
            return request.getInputStream();
        }
        
        return null;
    }
    
    /**
     * 获取 MultipartFile
     */
    private MultipartFile getMultipartFile(HttpServletRequest request, String fieldName) {
        if (request instanceof MultipartHttpServletRequest multipartRequest) {
            return multipartRequest.getFile(fieldName);
        }
        return null;
    }
    
    /**
     * 获取读取器实例
     */
    private ExcelReader getReader(ExcelImport annotation) {
        try {
            return applicationContext.getBean(annotation.reader());
        } catch (Exception e) {
            log.error("获取 Excel 读取器失败，将使用默认读取器", e);
            throw new ExcelIOException("获取 Excel 读取器失败", e);
        }
    }
}


package com.chatlabs.cdev.resolver;

import com.chatlabs.cdev.annotation.ExcelImport;
import com.chatlabs.cdev.config.ExcelIOProperties;
import com.chatlabs.cdev.exception.ExcelIOException;
import com.chatlabs.cdev.exception.ExcelParseException;
import com.chatlabs.cdev.extractor.InputStreamExtractor;
import com.chatlabs.cdev.reader.ExcelReader;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Excel导入参数解析器
 * 支持多种输入流获取方式，可通过注解指定或自动选择
 * 
 * @author DD
 */
@Slf4j
public class ExcelImportArgumentResolver implements HandlerMethodArgumentResolver {
    
    private final ApplicationContext applicationContext;
    private final ExcelIOProperties properties;
    private List<InputStreamExtractor> extractors;
    
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
        if (annotation == null) return null;
        
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) throw new ExcelIOException("无法获取HttpServletRequest");
        
        try {
            // 获取输入流
            InputStream inputStream = extractInputStream(request, annotation);
            if (inputStream == null) {
                if (annotation.required()) {
                    throw new ExcelParseException("未找到文件: " + annotation.value());
                }
                return null;
            }
            
            // 检查文件大小
            if (request.getContentLength() > properties.getMaxFileSize()) {
                throw new ExcelParseException("文件超出限制: " + properties.getMaxFileSize() + " bytes");
            }
            
            // 根据参数类型处理
            Class<?> paramType = parameter.getParameterType();
            if (MultipartFile.class.isAssignableFrom(paramType)) {
                return getMultipartFile(request, annotation.value());
            }
            if (InputStream.class.isAssignableFrom(paramType)) {
                return inputStream;
            }
            if (byte[].class.isAssignableFrom(paramType)) {
                return inputStream.readAllBytes();
            }
            
            // 解析为对象列表
            ExcelReader reader = applicationContext.getBean(annotation.reader());
            List<?> result = reader.read(inputStream, annotation.dataClass());
            log.debug("解析完成: {}条", result.size());
            return result;
        } catch (ExcelIOException e) {
            throw e;
        } catch (Exception e) {
            throw new ExcelParseException("解析失败", e);
        }
    }
    
    /**
     * 提取输入流
     * 优先使用注解指定的提取器，否则自动选择
     */
    private InputStream extractInputStream(HttpServletRequest request, ExcelImport annotation) throws Exception {
        String fieldName = annotation.value();
        Class<? extends InputStreamExtractor>[] specifiedExtractors = annotation.extractors();
        
        // 如果注解指定了提取器，只使用指定的
        if (specifiedExtractors.length > 0) {
            for (Class<? extends InputStreamExtractor> extractorClass : specifiedExtractors) {
                InputStreamExtractor extractor = applicationContext.getBean(extractorClass);
                if (extractor.supports(request, fieldName)) {
                    InputStream is = extractor.extract(request, fieldName);
                    if (is != null) {
                        log.debug("使用指定提取器: {}", extractorClass.getSimpleName());
                        return is;
                    }
                }
            }
            return null;
        }
        
        // 自动选择：按优先级尝试所有已注册的提取器
        List<InputStreamExtractor> allExtractors = getAllExtractors();
        for (InputStreamExtractor extractor : allExtractors) {
            if (extractor.supports(request, fieldName)) {
                InputStream is = extractor.extract(request, fieldName);
                if (is != null) {
                    log.debug("自动选择提取器: {}", extractor.getClass().getSimpleName());
                    return is;
                }
            }
        }
        
        return null;
    }
    
    /**
     * 获取所有已注册的提取器，按优先级排序
     */
    private List<InputStreamExtractor> getAllExtractors() {
        if (extractors == null) {
            extractors = new ArrayList<>(applicationContext.getBeansOfType(InputStreamExtractor.class).values());
            extractors.sort(Comparator.comparingInt(InputStreamExtractor::getOrder));
            log.debug("已注册{}个输入流提取器", extractors.size());
        }
        return extractors;
    }
    
    private MultipartFile getMultipartFile(HttpServletRequest request, String fieldName) {
        return request instanceof MultipartHttpServletRequest multipartRequest 
                ? multipartRequest.getFile(fieldName) : null;
    }
}

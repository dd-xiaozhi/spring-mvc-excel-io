package com.chatlabs.cdev.handler;

import com.chatlabs.cdev.annotation.ExcelExport;
import com.chatlabs.cdev.exception.ExcelIOException;
import com.chatlabs.cdev.processor.ExportDataProcessor;
import com.chatlabs.cdev.wrapper.ResponseWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Excel 导出处理器
 * 负责处理导出请求和异步导出
 *
 * @author DD
 */
@Component
public class ExcelExportHandler {

    private static final Logger log = LoggerFactory.getLogger(ExcelExportHandler.class);

    @Resource
    private ApplicationContext applicationContext;
    
    @Resource
    private List<ResponseWrapper> responseWrappers;

    /**
     * 处理导出请求
     */
    public void handleExport(HandlerMethod handlerMethod,
                             Object[] args,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception {

        Method method = handlerMethod.getMethod();
        ExcelExport annotation = method.getAnnotation(ExcelExport.class);

        if (annotation == null) {
            throw new ExcelIOException("未找到 @ExcelExport 注解");
        }

        // 获取数据
        List<?> data = obtainData(handlerMethod, args, annotation);

        // 获取处理器
        ExportDataProcessor processor = getProcessor(annotation);

        // 执行导出
        processor.process(data, annotation.dataClass(),
                annotation.fileName(), annotation.sheetName(), response);
    }

    /**
     * 异步处理导出请求
     */
    @Async("excelExportExecutor")
    public void handleExportAsync(HandlerMethod handlerMethod, Object[] args,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {

        log.info("开始异步导出任务: method={}", handlerMethod.getMethod().getName());

        try {
            handleExport(handlerMethod, args, request, response);
            log.info("异步导出任务完成: method={}", handlerMethod.getMethod().getName());
        } catch (Exception e) {
            log.error("异步导出任务失败: method={}", handlerMethod.getMethod().getName(), e);
            throw e;
        }
    }

    /**
     * 获取要导出的数据
     */
    private List<?> obtainData(HandlerMethod handlerMethod, Object[] args, ExcelExport annotation) throws Exception {
        if (!annotation.reuseMethod()) {
            return Collections.emptyList();
        }

        // 调用原方法获取数据
        Object controller = handlerMethod.getBean();
        Method method = handlerMethod.getMethod();
        Object result = method.invoke(controller, args);

        if (result == null) {
            return Collections.emptyList();
        }

        // 使用响应解析器链解析数据
        Object unwrapped = unwrapResponse(result);

        // 确保数据是 List 类型
        if (unwrapped instanceof List) {
            return (List<?>) unwrapped;
        }

        throw new ExcelIOException("方法返回值必须是 List 类型或可解析为 List 的包装类型");
    }

    /**
     * 使用责任链模式解封装响应数据
     */
    private Object unwrapResponse(Object result) {
        if (responseWrappers == null || responseWrappers.isEmpty()) {
            return result;
        }
        
        // 按优先级排序
        responseWrappers.sort(Comparator.comparingInt(ResponseWrapper::getOrder));
        
        // 找到第一个支持的包装器进行解封装
        for (ResponseWrapper wrapper : responseWrappers) {
            if (wrapper.supports(result)) {
                return wrapper.unwrap(result);
            }
        }
        
        return result;
    }

    /**
     * 获取处理器实例
     */
    private ExportDataProcessor getProcessor(ExcelExport annotation) {
        try {
            Class<? extends ExportDataProcessor> processorClass = annotation.processor();
            return applicationContext.getBean(processorClass);
        } catch (Exception e) {
            log.error("获取导出处理器失败，将使用默认处理器", e);
            throw new ExcelIOException("获取导出处理器失败", e);
        }
    }
}


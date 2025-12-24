package com.chatlabs.cdev.handler;

import com.chatlabs.cdev.annotation.ExcelExport;
import com.chatlabs.cdev.exception.ExcelIOException;
import com.chatlabs.cdev.processor.ExportDataProcessor;
import com.chatlabs.cdev.wrapper.ResponseWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Excel导出处理器
 *
 * @author DD
 */
@Slf4j
@Component
public class ExcelExportHandler {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private List<ResponseWrapper> responseWrappers;

    public void handleExport(HandlerMethod handlerMethod,
                             Object[] args,
                             HttpServletRequest request,
                             HttpServletResponse response) throws Exception {
        ExcelExport annotation = handlerMethod.getMethod().getAnnotation(ExcelExport.class);
        if (annotation == null) {
            throw new ExcelIOException("未找到 @ExcelExport 注解");
        }

        List<?> data = obtainData(handlerMethod, args, annotation);
        ExportDataProcessor processor = applicationContext.getBean(annotation.processor());
        processor.process(data, annotation.dataClass(), annotation.fileName(), annotation.sheetName(), response);
    }

    @Async("excelExportExecutor")
    public void handleExportAsync(HandlerMethod handlerMethod, Object[] args,
                                  HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("异步导出: {}", handlerMethod.getMethod().getName());
        handleExport(handlerMethod, args, request, response);
    }

    private List<?> obtainData(HandlerMethod handlerMethod, Object[] args, ExcelExport annotation) throws Exception {
        if (!annotation.reuseMethod()) {
            return Collections.emptyList();
        }

        Method method = handlerMethod.getMethod();
        Object result = method.invoke(handlerMethod.getBean(), args);
        if (result == null) {
            return Collections.emptyList();
        }

        Object unwrapped = unwrapResponse(result);
        if (unwrapped instanceof List) {
            return (List<?>) unwrapped;
        }
        throw new ExcelIOException("返回值必须是List类型");
    }

    private Object unwrapResponse(Object result) {
        if (responseWrappers == null || responseWrappers.isEmpty()) {
            return result;
        }
        responseWrappers.sort(Comparator.comparingInt(ResponseWrapper::getOrder));
        for (ResponseWrapper wrapper : responseWrappers) {
            if (wrapper.supports(result.getClass())) {
                return wrapper.unwrap(result);
            }
        }
        return result;
    }
}


package com.chatlabs.cdev.interceptor;

import com.chatlabs.cdev.annotation.ExcelExport;
import com.chatlabs.cdev.handler.ExcelExportHandler;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Excel导出拦截器
 *
 * @author DD
 */
@Slf4j
@Component
public class ExcelExportInterceptor implements HandlerInterceptor {

    private static final String EXPORT_FLAG = "_excel_export_flag";
    private static final String EXPORT_ARGS = "_excel_export_args";

    @Resource
    private ExcelExportHandler exportHandler;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        ExcelExport annotation = handlerMethod.getMethodAnnotation(ExcelExport.class);
        if (annotation == null) {
            return true;
        }

        String path = request.getRequestURI();
        if (path.endsWith("/export") || isCustomExportPath(path, annotation)) {
            request.setAttribute(EXPORT_FLAG, true);
            log.debug("导出请求: {}", path);
        }
        return true;
    }
    
    private boolean isCustomExportPath(String requestPath, ExcelExport annotation) {
        String customPath = annotation.value();
        if (customPath != null && !customPath.isEmpty()) {
            String pattern = customPath.replaceAll("\\{[^}]+}", "[^/]+");
            return requestPath.matches(".*" + pattern + "$");
        }
        return false;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler,
                           ModelAndView modelAndView) throws Exception {
        Boolean isExport = (Boolean) request.getAttribute(EXPORT_FLAG);
        if (isExport == null || !isExport || !(handler instanceof HandlerMethod handlerMethod)) {
            return;
        }

        ExcelExport annotation = handlerMethod.getMethodAnnotation(ExcelExport.class);
        if (annotation == null) {
            return;
        }

        Object[] args = (Object[]) request.getAttribute(EXPORT_ARGS);
        if (annotation.async()) {
            exportHandler.handleExportAsync(handlerMethod, args, request, response);
        } else {
            exportHandler.handleExport(handlerMethod, args, request, response);
        }

        if (modelAndView != null) {
            modelAndView.clear();
        }
    }
}


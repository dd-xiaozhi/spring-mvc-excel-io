package com.chatlabs.cdev.interceptor;

import com.chatlabs.cdev.annotation.ExcelExport;
import com.chatlabs.cdev.handler.ExcelExportHandler;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Excel 导出拦截器
 * 拦截导出请求并处理
 *
 * @author DD
 */
@Component
public class ExcelExportInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ExcelExportInterceptor.class);

    private static final String EXPORT_FLAG_ATTR = "_excel_export_flag";
    private static final String EXPORT_ARGS_ATTR = "_excel_export_args";

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

        // 判断是否是导出路径
        String requestPath = request.getRequestURI();
        boolean isExportPath = requestPath.endsWith("/export") || isCustomExportPath(requestPath, annotation);
        
        // 只有访问导出路径时才标记为导出请求
        if (isExportPath) {
            request.setAttribute(EXPORT_FLAG_ATTR, true);
            log.debug("检测到导出请求: path={}", requestPath);
        }

        return true;
    }
    
    /**
     * 判断是否是自定义导出路径
     */
    private boolean isCustomExportPath(String requestPath, ExcelExport annotation) {
        String customPath = annotation.value();
        if (customPath != null && !customPath.isEmpty()) {
            // 移除路径变量进行匹配
            String pathPattern = customPath.replaceAll("\\{[^}]+\\}", "[^/]+");
            return requestPath.matches(".*" + pathPattern + "$");
        }
        return false;
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler,
                           ModelAndView modelAndView) throws Exception {

        Boolean isExport = (Boolean) request.getAttribute(EXPORT_FLAG_ATTR);
        if (isExport == null || !isExport) {
            return;
        }

        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return;
        }

        ExcelExport annotation = handlerMethod.getMethodAnnotation(ExcelExport.class);
        if (annotation == null) {
            return;
        }

        // 获取方法参数（需要在 Controller 执行前保存）
        Object[] args = (Object[]) request.getAttribute(EXPORT_ARGS_ATTR);

        // 处理导出
        if (annotation.async()) {
            exportHandler.handleExportAsync(handlerMethod, args, request, response);
        } else {
            exportHandler.handleExport(handlerMethod, args, request, response);
        }

        // 清除视图，因为已经直接写入响应
        if (modelAndView != null) {
            modelAndView.clear();
        }
    }
}


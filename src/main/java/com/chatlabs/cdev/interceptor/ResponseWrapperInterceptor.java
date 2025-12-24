package com.chatlabs.cdev.interceptor;

import com.chatlabs.cdev.wrapper.ResponseWrapper;
import jakarta.annotation.Resource;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Comparator;
import java.util.List;

/**
 * 响应包装拦截器
 * 自动包装Controller返回的数据
 *
 * @author DD
 */
@RestControllerAdvice(basePackages = "com.chatlabs.cdev.example")
public class ResponseWrapperInterceptor implements ResponseBodyAdvice<Object> {

    private static final Logger log = LoggerFactory.getLogger(ResponseWrapperInterceptor.class);

    @Resource
    private List<ResponseWrapper> responseWrappers;

    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> returnParamterType = returnType.getParameterType();
        return responseWrappers.stream()
                .anyMatch(wrapper -> wrapper.supports(returnParamterType));
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {

        if (body == null) {
            return null;
        }

        // 使用责任链模式进行封装
        return wrapResponse(body);
    }

    /**
     * 使用责任链模式封装响应
     */
    private Object wrapResponse(Object body) {
        if (responseWrappers == null || responseWrappers.isEmpty()) {
            return body;
        }

        // 按优先级排序
        responseWrappers.sort(Comparator.comparingInt(ResponseWrapper::getOrder));

        // 使用第一个非默认包装器进行封装
        for (ResponseWrapper wrapper : responseWrappers) {
            if (wrapper.getOrder() < Integer.MAX_VALUE) {
                log.debug("使用 {} 封装响应", wrapper.getClass().getSimpleName());
                return wrapper.wrap(body);
            }
        }

        // 如果没有自定义包装器，返回原数据
        return body;
    }
}


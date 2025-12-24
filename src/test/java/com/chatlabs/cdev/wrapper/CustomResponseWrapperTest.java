package com.chatlabs.cdev.wrapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 自定义ResponseWrapper测试 - 测试责任链模式
 */
@DisplayName("ResponseWrapper责任链测试")
class CustomResponseWrapperTest {

    @Test
    @DisplayName("责任链按优先级排序")
    void testChainOrder() {
        // 创建多个wrapper
        ResponseWrapper high = new HighPriorityWrapper();
        ResponseWrapper low = new LowPriorityWrapper();
        ResponseWrapper defaultWrapper = new DefaultResponseWrapper();

        List<ResponseWrapper> wrappers = Arrays.asList(defaultWrapper, low, high);
        wrappers.sort(Comparator.comparingInt(ResponseWrapper::getOrder));

        // 验证排序
        assertEquals(high, wrappers.get(0));
        assertEquals(low, wrappers.get(1));
        assertEquals(defaultWrapper, wrappers.get(2));
    }

    @Test
    @DisplayName("责任链选择第一个支持的wrapper")
    void testChainSelection() {
        ResponseWrapper high = new HighPriorityWrapper();
        ResponseWrapper low = new LowPriorityWrapper();
        ResponseWrapper defaultWrapper = new DefaultResponseWrapper();

        List<ResponseWrapper> wrappers = Arrays.asList(high, low, defaultWrapper);
        wrappers.sort(Comparator.comparingInt(ResponseWrapper::getOrder));

        // 测试Result类型 - 高优先级wrapper支持
        Result<String> result = new Result<>("data");
        ResponseWrapper selected = findWrapper(wrappers, result);
        assertInstanceOf(HighPriorityWrapper.class, selected);

        // 测试String类型 - 低优先级wrapper支持
        String str = "test";
        selected = findWrapper(wrappers, str);
        assertInstanceOf(LowPriorityWrapper.class, selected);
        
        // 测试其他类型 - 默认wrapper兜底
        Integer num = 123;
        selected = findWrapper(wrappers, num);
        assertInstanceOf(DefaultResponseWrapper.class, selected);
    }

    @Test
    @DisplayName("自定义wrapper解封装")
    void testCustomUnwrap() {
        HighPriorityWrapper wrapper = new HighPriorityWrapper();
        Result<List<String>> result = new Result<>(Arrays.asList("a", "b", "c"));

        Object unwrapped = wrapper.unwrap(result);
        assertInstanceOf(List.class, unwrapped);
        assertEquals(3, ((List<?>) unwrapped).size());
    }

    @Test
    @DisplayName("自定义wrapper封装")
    void testCustomWrap() {
        HighPriorityWrapper wrapper = new HighPriorityWrapper();
        List<String> data = Arrays.asList("a", "b", "c");

        Object wrapped = wrapper.wrap(data);
        assertInstanceOf(Result.class, wrapped);
        assertEquals(data, ((Result<?>) wrapped).getData());
    }

    private ResponseWrapper findWrapper(List<ResponseWrapper> wrappers, Object result) {
        for (ResponseWrapper wrapper : wrappers) {
            if (wrapper.supports(result.getClass())) {
                return wrapper;
            }
        }
        return null;
    }

    // 测试用Result类
    static class Result<T> {
        private T data;

        public Result(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    // 高优先级wrapper
    static class HighPriorityWrapper implements ResponseWrapper {
        @Override
        public boolean supports(Class<?> clazz) {
            return Result.class.isAssignableFrom(clazz);
        }

        @Override
        public Object unwrap(Object result) {
            return ((Result<?>) result).getData();
        }

        @Override
        public Object wrap(Object data) {
            return new Result<>(data);
        }

        @Override
        public int getOrder() {
            return 100;
        }
    }

    // 低优先级wrapper
    static class LowPriorityWrapper implements ResponseWrapper {
        @Override
        public boolean supports(Class<?> clazz) {
            return String.class.isAssignableFrom(clazz);
        }

        @Override
        public Object unwrap(Object result) {
            return result;
        }

        @Override
        public Object wrap(Object data) {
            return data;
        }

        @Override
        public int getOrder() {
            return 500;
        }
    }
}

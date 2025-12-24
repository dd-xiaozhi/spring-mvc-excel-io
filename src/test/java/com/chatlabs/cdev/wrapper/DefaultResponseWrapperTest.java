package com.chatlabs.cdev.wrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DefaultResponseWrapper 单元测试
 */
@DisplayName("DefaultResponseWrapper 单元测试")
class DefaultResponseWrapperTest {

    private DefaultResponseWrapper wrapper;

    @BeforeEach
    void setUp() {
        wrapper = new DefaultResponseWrapper();
    }

    @Test
    @DisplayName("supports方法始终返回true")
    void testSupportsAlwaysTrue() {
        assertTrue(wrapper.supports(String.class));
        assertTrue(wrapper.supports(Integer.class));
        assertTrue(wrapper.supports(List.class));
        assertTrue(wrapper.supports(Object.class));
    }

    @Test
    @DisplayName("unwrap方法返回原始数据")
    void testUnwrapReturnsOriginal() {
        String str = "test";
        assertEquals(str, wrapper.unwrap(str));

        List<Integer> list = Arrays.asList(1, 2, 3);
        assertEquals(list, wrapper.unwrap(list));

        assertNull(wrapper.unwrap(null));
    }

    @Test
    @DisplayName("wrap方法返回原始数据")
    void testWrapReturnsOriginal() {
        String str = "test";
        assertEquals(str, wrapper.wrap(str));

        List<Integer> list = Arrays.asList(1, 2, 3);
        assertEquals(list, wrapper.wrap(list));
    }

    @Test
    @DisplayName("getOrder返回最大值")
    void testGetOrderReturnsMaxValue() {
        assertEquals(Integer.MAX_VALUE, wrapper.getOrder());
    }
}

package com.chatlabs.cdev.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExcelIOProperties 单元测试
 */
@DisplayName("ExcelIOProperties 单元测试")
class ExcelIOPropertiesTest {

    @Test
    @DisplayName("默认值正确")
    void testDefaultValues() {
        ExcelIOProperties properties = new ExcelIOProperties();

        assertTrue(properties.isEnabled());
        assertEquals("export", properties.getDefaultFileName());
        assertEquals("Sheet1", properties.getDefaultSheetName());
        assertEquals("yyyy-MM-dd HH:mm:ss", properties.getDateFormat());
        assertEquals(10 * 1024 * 1024, properties.getMaxFileSize()); // 10MB
        assertEquals(2, properties.getAsyncCorePoolSize());
        assertEquals(5, properties.getAsyncMaxPoolSize());
        assertEquals(100, properties.getAsyncQueueCapacity());
    }

    @Test
    @DisplayName("设置和获取属性")
    void testSetAndGetProperties() {
        ExcelIOProperties properties = new ExcelIOProperties();

        properties.setEnabled(false);
        assertFalse(properties.isEnabled());

        properties.setDefaultFileName("custom");
        assertEquals("custom", properties.getDefaultFileName());

        properties.setDefaultSheetName("CustomSheet");
        assertEquals("CustomSheet", properties.getDefaultSheetName());

        properties.setDateFormat("yyyy/MM/dd");
        assertEquals("yyyy/MM/dd", properties.getDateFormat());

        properties.setMaxFileSize(20 * 1024 * 1024);
        assertEquals(20 * 1024 * 1024, properties.getMaxFileSize());

        properties.setAsyncCorePoolSize(4);
        assertEquals(4, properties.getAsyncCorePoolSize());

        properties.setAsyncMaxPoolSize(10);
        assertEquals(10, properties.getAsyncMaxPoolSize());

        properties.setAsyncQueueCapacity(200);
        assertEquals(200, properties.getAsyncQueueCapacity());
    }
}

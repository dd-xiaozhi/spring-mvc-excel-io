package com.chatlabs.cdev.integration;

import com.chatlabs.cdev.ExcelIOApplication;
import com.chatlabs.cdev.config.ExcelIOProperties;
import com.chatlabs.cdev.handler.ExcelExportHandler;
import com.chatlabs.cdev.interceptor.ExcelExportInterceptor;
import com.chatlabs.cdev.processor.DefaultExportProcessor;
import com.chatlabs.cdev.reader.DefaultExcelReader;
import com.chatlabs.cdev.wrapper.DefaultResponseWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 自动配置集成测试
 */
@SpringBootTest(classes = ExcelIOApplication.class)
@DisplayName("自动配置集成测试")
class AutoConfigurationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("ExcelIOProperties Bean已注册")
    void testExcelIOPropertiesBean() {
        ExcelIOProperties properties = applicationContext.getBean(ExcelIOProperties.class);
        assertNotNull(properties);
        assertTrue(properties.isEnabled());
    }

    @Test
    @DisplayName("DefaultExportProcessor Bean已注册")
    void testDefaultExportProcessorBean() {
        DefaultExportProcessor processor = applicationContext.getBean(DefaultExportProcessor.class);
        assertNotNull(processor);
    }

    @Test
    @DisplayName("DefaultExcelReader Bean已注册")
    void testDefaultExcelReaderBean() {
        DefaultExcelReader reader = applicationContext.getBean(DefaultExcelReader.class);
        assertNotNull(reader);
    }

    @Test
    @DisplayName("DefaultResponseWrapper Bean已注册")
    void testDefaultResponseWrapperBean() {
        DefaultResponseWrapper wrapper = applicationContext.getBean(DefaultResponseWrapper.class);
        assertNotNull(wrapper);
    }

    @Test
    @DisplayName("ExcelExportHandler Bean已注册")
    void testExcelExportHandlerBean() {
        ExcelExportHandler handler = applicationContext.getBean(ExcelExportHandler.class);
        assertNotNull(handler);
    }

    @Test
    @DisplayName("ExcelExportInterceptor Bean已注册")
    void testExcelExportInterceptorBean() {
        ExcelExportInterceptor interceptor = applicationContext.getBean(ExcelExportInterceptor.class);
        assertNotNull(interceptor);
    }

    @Test
    @DisplayName("配置属性默认值正确")
    void testDefaultProperties() {
        ExcelIOProperties properties = applicationContext.getBean(ExcelIOProperties.class);
        assertEquals(10 * 1024 * 1024, properties.getMaxFileSize());
        assertEquals("export", properties.getDefaultFileName());
        assertEquals("Sheet1", properties.getDefaultSheetName());
    }
}

package com.chatlabs.cdev.handler;

import com.chatlabs.cdev.annotation.ExcelExport;
import com.chatlabs.cdev.dto.TestUserDTO;
import com.chatlabs.cdev.exception.ExcelIOException;
import com.chatlabs.cdev.processor.DefaultExportProcessor;
import com.chatlabs.cdev.wrapper.DefaultResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * ExcelExportHandler 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExcelExportHandler 单元测试")
class ExcelExportHandlerTest {

    private ExcelExportHandler handler;

    @Mock
    private ApplicationContext applicationContext;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() throws Exception {
        handler = new ExcelExportHandler();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        // 注入mock的ApplicationContext
        Field contextField = ExcelExportHandler.class.getDeclaredField("applicationContext");
        contextField.setAccessible(true);
        contextField.set(handler, applicationContext);

        // 注入ResponseWrapper列表
        Field wrappersField = ExcelExportHandler.class.getDeclaredField("responseWrappers");
        wrappersField.setAccessible(true);
        wrappersField.set(handler, List.of(new DefaultResponseWrapper()));
    }

    @Test
    @DisplayName("处理导出请求 - 无注解抛出异常")
    void testHandleExportWithoutAnnotation() throws Exception {
        TestController controller = new TestController();
        Method method = TestController.class.getMethod("noAnnotation");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);

        assertThrows(ExcelIOException.class, 
                () -> handler.handleExport(handlerMethod, new Object[]{}, request, response));
    }

    @Test
    @DisplayName("处理导出请求 - 返回null数据")
    void testHandleExportWithNullData() throws Exception {
        TestController controller = new TestController();
        Method method = TestController.class.getMethod("exportNull");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);

        DefaultExportProcessor processor = new DefaultExportProcessor();
        when(applicationContext.getBean(DefaultExportProcessor.class)).thenReturn(processor);

        handler.handleExport(handlerMethod, new Object[]{}, request, response);

        // 应该正常处理，导出空数据
        assertTrue(response.getContentType().startsWith("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    }

    // 测试用Controller
    public static class TestController {

        @ExcelExport(fileName = "测试", dataClass = TestUserDTO.class)
        public List<TestUserDTO> exportUsers() {
            return Arrays.asList(
                    new TestUserDTO(1L, "张三", "zhangsan@test.com", 25),
                    new TestUserDTO(2L, "李四", "lisi@test.com", 30)
            );
        }

        @ExcelExport(fileName = "空数据", dataClass = TestUserDTO.class)
        public List<TestUserDTO> exportNull() {
            return null;
        }

        public List<TestUserDTO> noAnnotation() {
            return List.of();
        }
    }
}

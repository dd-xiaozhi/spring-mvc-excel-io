package com.chatlabs.cdev.interceptor;

import com.chatlabs.cdev.annotation.ExcelExport;
import com.chatlabs.cdev.dto.TestUserDTO;
import com.chatlabs.cdev.handler.ExcelExportHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ExcelExportInterceptor 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExcelExportInterceptor 单元测试")
class ExcelExportInterceptorTest {

    private ExcelExportInterceptor interceptor;

    @Mock
    private ExcelExportHandler exportHandler;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() throws Exception {
        interceptor = new ExcelExportInterceptor();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        // 注入mock的handler
        Field handlerField = ExcelExportInterceptor.class.getDeclaredField("exportHandler");
        handlerField.setAccessible(true);
        handlerField.set(interceptor, exportHandler);
    }

    @Test
    @DisplayName("preHandle - 非HandlerMethod直接放行")
    void testPreHandleWithNonHandlerMethod() {
        boolean result = interceptor.preHandle(request, response, new Object());
        assertTrue(result);
    }

    @Test
    @DisplayName("preHandle - 无ExcelExport注解直接放行")
    void testPreHandleWithoutAnnotation() throws Exception {
        TestController controller = new TestController();
        Method method = TestController.class.getMethod("noAnnotation");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);

        boolean result = interceptor.preHandle(request, response, handlerMethod);
        assertTrue(result);
        assertNull(request.getAttribute("_excel_export_flag"));
    }

    @Test
    @DisplayName("preHandle - 导出路径设置标记")
    void testPreHandleWithExportPath() throws Exception {
        TestController controller = new TestController();
        Method method = TestController.class.getMethod("exportUsers");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);

        request.setRequestURI("/users/export");

        boolean result = interceptor.preHandle(request, response, handlerMethod);
        assertTrue(result);
        assertEquals(true, request.getAttribute("_excel_export_flag"));
    }

    @Test
    @DisplayName("preHandle - 非导出路径不设置标记")
    void testPreHandleWithNonExportPath() throws Exception {
        TestController controller = new TestController();
        Method method = TestController.class.getMethod("exportUsers");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);

        request.setRequestURI("/users");

        boolean result = interceptor.preHandle(request, response, handlerMethod);
        assertTrue(result);
        assertNull(request.getAttribute("_excel_export_flag"));
    }

    @Test
    @DisplayName("preHandle - 自定义导出路径")
    void testPreHandleWithCustomExportPath() throws Exception {
        TestController controller = new TestController();
        Method method = TestController.class.getMethod("customPath");
        HandlerMethod handlerMethod = new HandlerMethod(controller, method);

        request.setRequestURI("/users/download");

        boolean result = interceptor.preHandle(request, response, handlerMethod);
        assertTrue(result);
        assertEquals(true, request.getAttribute("_excel_export_flag"));
    }

    // 测试用Controller
    public static class TestController {

        @ExcelExport(fileName = "测试", dataClass = TestUserDTO.class)
        public List<TestUserDTO> exportUsers() {
            return List.of();
        }

        @ExcelExport(value = "/users/download", fileName = "自定义", dataClass = TestUserDTO.class)
        public List<TestUserDTO> customPath() {
            return List.of();
        }

        public List<TestUserDTO> noAnnotation() {
            return List.of();
        }
    }
}

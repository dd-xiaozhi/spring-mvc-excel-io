package com.chatlabs.cdev.resolver;

import com.alibaba.excel.EasyExcel;
import com.chatlabs.cdev.annotation.ExcelImport;
import com.chatlabs.cdev.config.ExcelIOProperties;
import com.chatlabs.cdev.dto.TestUserDTO;
import com.chatlabs.cdev.exception.ExcelParseException;
import com.chatlabs.cdev.extractor.InputStreamExtractor;
import com.chatlabs.cdev.extractor.MultipartInputStreamExtractor;
import com.chatlabs.cdev.reader.DefaultExcelReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ExcelImportArgumentResolver 单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ExcelImportArgumentResolver 单元测试")
class ExcelImportArgumentResolverTest {

    private ExcelImportArgumentResolver resolver;

    @Mock
    private ApplicationContext applicationContext;

    private ExcelIOProperties properties;
    private MultipartInputStreamExtractor multipartExtractor;

    @BeforeEach
    void setUp() {
        properties = new ExcelIOProperties();
        multipartExtractor = new MultipartInputStreamExtractor();
        resolver = new ExcelImportArgumentResolver(applicationContext, properties);
    }

    @Test
    @DisplayName("supportsParameter - 有ExcelImport注解返回true")
    void testSupportsParameterWithAnnotation() throws Exception {
        Method method = TestController.class.getMethod("importUsers", List.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        assertTrue(resolver.supportsParameter(parameter));
    }

    @Test
    @DisplayName("supportsParameter - 无ExcelImport注解返回false")
    void testSupportsParameterWithoutAnnotation() throws Exception {
        Method method = TestController.class.getMethod("noAnnotation", List.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        assertFalse(resolver.supportsParameter(parameter));
    }

    @Test
    @DisplayName("resolveArgument - 正常解析Excel文件")
    void testResolveArgumentNormal() throws Exception {
        // 每次测试创建新的resolver实例，避免缓存问题
        resolver = new ExcelImportArgumentResolver(applicationContext, properties);
        
        // 准备测试数据
        List<TestUserDTO> testData = List.of(
                new TestUserDTO(1L, "张三", "zhangsan@test.com", 25)
        );
        byte[] excelContent = createExcelContent(testData);

        // 准备请求
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelContent);
        request.addFile(file);

        NativeWebRequest webRequest = new ServletWebRequest(request);
        Method method = TestController.class.getMethod("importUsers", List.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // Mock extractors - 自动选择模式，返回已注册的提取器
        when(applicationContext.getBeansOfType(InputStreamExtractor.class))
                .thenReturn(Map.of("multipartExtractor", multipartExtractor));
        // Mock reader
        when(applicationContext.getBean(DefaultExcelReader.class)).thenReturn(new DefaultExcelReader());

        // 执行
        Object result = resolver.resolveArgument(parameter, null, webRequest, null);

        // 验证
        assertNotNull(result);
        assertInstanceOf(List.class, result);
        List<?> list = (List<?>) result;
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("resolveArgument - 文件不存在且required=true抛出异常")
    void testResolveArgumentRequiredFileNotFound() throws Exception {
        resolver = new ExcelImportArgumentResolver(applicationContext, properties);
        
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        NativeWebRequest webRequest = new ServletWebRequest(request);
        Method method = TestController.class.getMethod("importUsers", List.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // Mock extractors - 自动选择模式
        when(applicationContext.getBeansOfType(InputStreamExtractor.class))
                .thenReturn(Map.of("multipartExtractor", multipartExtractor));

        assertThrows(ExcelParseException.class,
                () -> resolver.resolveArgument(parameter, null, webRequest, null));
    }

    @Test
    @DisplayName("resolveArgument - 文件不存在且required=false返回null")
    void testResolveArgumentOptionalFileNotFound() throws Exception {
        resolver = new ExcelImportArgumentResolver(applicationContext, properties);
        
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        NativeWebRequest webRequest = new ServletWebRequest(request);
        Method method = TestController.class.getMethod("importOptional", List.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // Mock extractors - 自动选择模式
        when(applicationContext.getBeansOfType(InputStreamExtractor.class))
                .thenReturn(Map.of("multipartExtractor", multipartExtractor));

        Object result = resolver.resolveArgument(parameter, null, webRequest, null);
        assertNull(result);
    }

    @Test
    @DisplayName("resolveArgument - 文件超出大小限制")
    void testResolveArgumentFileTooLarge() throws Exception {
        properties.setMaxFileSize(100); // 设置很小的限制
        resolver = new ExcelImportArgumentResolver(applicationContext, properties);

        List<TestUserDTO> testData = List.of(
                new TestUserDTO(1L, "张三", "zhangsan@test.com", 25)
        );
        byte[] excelContent = createExcelContent(testData);

        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        request.setContentType("multipart/form-data");
        request.setContent(new byte[1000]); // 模拟大文件
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelContent);
        request.addFile(file);

        NativeWebRequest webRequest = new ServletWebRequest(request);
        Method method = TestController.class.getMethod("importUsers", List.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        // Mock extractors - 自动选择模式
        when(applicationContext.getBeansOfType(InputStreamExtractor.class))
                .thenReturn(Map.of("multipartExtractor", multipartExtractor));

        assertThrows(ExcelParseException.class,
                () -> resolver.resolveArgument(parameter, null, webRequest, null));
    }

    private byte[] createExcelContent(List<TestUserDTO> data) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, TestUserDTO.class).sheet("Sheet1").doWrite(data);
        return outputStream.toByteArray();
    }

    // 测试用Controller
    public static class TestController {

        public void importUsers(@ExcelImport(dataClass = TestUserDTO.class) List<TestUserDTO> users) {
        }

        public void importOptional(@ExcelImport(dataClass = TestUserDTO.class, required = false) List<TestUserDTO> users) {
        }

        public void noAnnotation(List<TestUserDTO> users) {
        }
    }
}

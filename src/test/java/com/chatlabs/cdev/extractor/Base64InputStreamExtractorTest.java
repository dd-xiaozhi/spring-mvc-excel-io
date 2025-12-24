package com.chatlabs.cdev.extractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.InputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Base64InputStreamExtractor 单元测试
 */
@DisplayName("Base64InputStreamExtractor 单元测试")
class Base64InputStreamExtractorTest {

    private Base64InputStreamExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new Base64InputStreamExtractor();
    }

    @Test
    @DisplayName("supports - JSON请求返回true")
    void testSupportsJsonRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        assertTrue(extractor.supports(request, "file"));
    }

    @Test
    @DisplayName("supports - JSON with charset返回true")
    void testSupportsJsonWithCharset() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json; charset=UTF-8");
        assertTrue(extractor.supports(request, "file"));
    }

    @Test
    @DisplayName("supports - 非JSON请求返回false")
    void testSupportsNonJsonRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("multipart/form-data");
        assertFalse(extractor.supports(request, "file"));
    }

    @Test
    @DisplayName("supports - 无ContentType返回false")
    void testSupportsNoContentType() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertFalse(extractor.supports(request, "file"));
    }

    @Test
    @DisplayName("extract - 正常提取Base64内容")
    void testExtractNormal() throws Exception {
        byte[] originalContent = "test excel content".getBytes();
        String base64Content = Base64.getEncoder().encodeToString(originalContent);
        String jsonBody = "{\"file\": \"" + base64Content + "\"}";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent(jsonBody.getBytes());

        InputStream result = extractor.extract(request, "file");

        assertNotNull(result);
        assertArrayEquals(originalContent, result.readAllBytes());
    }

    @Test
    @DisplayName("extract - 带data:前缀的Base64内容")
    void testExtractWithDataPrefix() throws Exception {
        byte[] originalContent = "test excel content".getBytes();
        String base64Content = Base64.getEncoder().encodeToString(originalContent);
        String dataUri = "data:application/vnd.ms-excel;base64," + base64Content;
        String jsonBody = "{\"file\": \"" + dataUri + "\"}";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent(jsonBody.getBytes());

        InputStream result = extractor.extract(request, "file");

        assertNotNull(result);
        assertArrayEquals(originalContent, result.readAllBytes());
    }

    @Test
    @DisplayName("extract - 字段不存在返回null")
    void testExtractFieldNotFound() throws Exception {
        String jsonBody = "{\"other\": \"value\"}";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent(jsonBody.getBytes());

        InputStream result = extractor.extract(request, "file");

        assertNull(result);
    }

    @Test
    @DisplayName("extract - 字段为null返回null")
    void testExtractFieldNull() throws Exception {
        String jsonBody = "{\"file\": null}";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent(jsonBody.getBytes());

        InputStream result = extractor.extract(request, "file");

        assertNull(result);
    }

    @Test
    @DisplayName("extract - 非JSON ContentType返回null")
    void testExtractNonJsonContentType() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("text/plain");
        request.setContent("test".getBytes());

        InputStream result = extractor.extract(request, "file");

        assertNull(result);
    }

    @Test
    @DisplayName("extract - 无效JSON返回null")
    void testExtractInvalidJson() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        request.setContent("invalid json".getBytes());

        InputStream result = extractor.extract(request, "file");

        assertNull(result);
    }

    @Test
    @DisplayName("getOrder - 返回200")
    void testGetOrder() {
        assertEquals(200, extractor.getOrder());
    }
}

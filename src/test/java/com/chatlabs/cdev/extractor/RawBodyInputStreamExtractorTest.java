package com.chatlabs.cdev.extractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RawBodyInputStreamExtractor 单元测试
 */
@DisplayName("RawBodyInputStreamExtractor 单元测试")
class RawBodyInputStreamExtractorTest {

    private RawBodyInputStreamExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new RawBodyInputStreamExtractor();
    }

    @Test
    @DisplayName("supports - xlsx ContentType返回true")
    void testSupportsXlsxContentType() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        assertTrue(extractor.supports(request, "file"));
    }

    @Test
    @DisplayName("supports - xls ContentType返回true")
    void testSupportsXlsContentType() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/vnd.ms-excel");
        assertTrue(extractor.supports(request, "file"));
    }

    @Test
    @DisplayName("supports - octet-stream ContentType返回true")
    void testSupportsOctetStreamContentType() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/octet-stream");
        assertTrue(extractor.supports(request, "file"));
    }

    @Test
    @DisplayName("supports - 其他ContentType返回false")
    void testSupportsOtherContentType() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/json");
        assertFalse(extractor.supports(request, "file"));
    }

    @Test
    @DisplayName("supports - 无ContentType返回false")
    void testSupportsNoContentType() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertFalse(extractor.supports(request, "file"));
    }

    @Test
    @DisplayName("extract - 正常提取请求体")
    void testExtractNormal() throws Exception {
        byte[] content = "test excel content".getBytes();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/octet-stream");
        request.setContent(content);

        InputStream result = extractor.extract(request, "file");

        assertNotNull(result);
        assertArrayEquals(content, result.readAllBytes());
    }

    @Test
    @DisplayName("extract - 空请求体返回null")
    void testExtractEmptyBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContentType("application/octet-stream");
        // 不设置content，contentLength为0

        InputStream result = extractor.extract(request, "file");

        assertNull(result);
    }

    @Test
    @DisplayName("getOrder - 返回300")
    void testGetOrder() {
        assertEquals(300, extractor.getOrder());
    }
}

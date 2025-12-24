package com.chatlabs.cdev.extractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MultipartInputStreamExtractor 单元测试
 */
@DisplayName("MultipartInputStreamExtractor 单元测试")
class MultipartInputStreamExtractorTest {

    private MultipartInputStreamExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new MultipartInputStreamExtractor();
    }

    @Test
    @DisplayName("supports - Multipart请求返回true")
    void testSupportsMultipartRequest() {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        assertTrue(extractor.supports(request, "file"));
    }

    @Test
    @DisplayName("supports - 普通请求返回false")
    void testSupportsNormalRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        assertFalse(extractor.supports(request, "file"));
    }

    @Test
    @DisplayName("extract - 正常提取文件流")
    void testExtractNormal() throws Exception {
        byte[] content = "test content".getBytes();
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", 
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", content);
        request.addFile(file);

        InputStream result = extractor.extract(request, "file");
        
        assertNotNull(result);
        assertArrayEquals(content, result.readAllBytes());
    }

    @Test
    @DisplayName("extract - 文件不存在返回null")
    void testExtractFileNotFound() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        
        InputStream result = extractor.extract(request, "file");
        
        assertNull(result);
    }

    @Test
    @DisplayName("extract - 空文件返回null")
    void testExtractEmptyFile() throws Exception {
        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", 
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[0]);
        request.addFile(file);

        InputStream result = extractor.extract(request, "file");
        
        assertNull(result);
    }

    @Test
    @DisplayName("extract - 非Multipart请求返回null")
    void testExtractNonMultipartRequest() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        
        InputStream result = extractor.extract(request, "file");
        
        assertNull(result);
    }

    @Test
    @DisplayName("getOrder - 返回100")
    void testGetOrder() {
        assertEquals(100, extractor.getOrder());
    }
}

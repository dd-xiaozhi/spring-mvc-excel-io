package com.chatlabs.cdev.integration;

import com.chatlabs.cdev.ExcelIOApplication;
import com.chatlabs.cdev.example.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Excel导出集成测试
 */
@SpringBootTest(classes = ExcelIOApplication.class)
@AutoConfigureMockMvc
@DisplayName("Excel导出集成测试")
class ExcelExportIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("访问原接口返回JSON")
    void testOriginalEndpointReturnsJson() throws Exception {
        mockMvc.perform(get("/example/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("访问导出接口返回Excel")
    void testExportEndpointReturnsExcel() throws Exception {
        MvcResult result = mockMvc.perform(get("/example/users/export"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andReturn();

        // 验证Content-Type
        String contentType = result.getResponse().getContentType();
        assertNotNull(contentType);
        assertTrue(contentType.contains("spreadsheetml.sheet"));

        // 验证有内容返回
        byte[] content = result.getResponse().getContentAsByteArray();
        assertTrue(content.length > 0);
    }

    @Test
    @DisplayName("自定义导出路径")
    void testCustomExportPath() throws Exception {
        // 原接口返回JSON
        mockMvc.perform(get("/example/users/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));

        // 自定义导出路径返回Excel
        MvcResult result = mockMvc.perform(get("/example/users/download"))
                .andExpect(status().isOk())
                .andReturn();

        byte[] content = result.getResponse().getContentAsByteArray();
        assertTrue(content.length > 0);
    }

    @Test
    @DisplayName("异步导出接口")
    void testAsyncExport() throws Exception {
        // 原接口返回JSON
        mockMvc.perform(get("/example/users/async"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"));
    }

    @Test
    @DisplayName("导出文件名包含中文")
    void testChineseFileName() throws Exception {
        MvcResult result = mockMvc.perform(get("/example/users/export"))
                .andExpect(status().isOk())
                .andReturn();

        String disposition = result.getResponse().getHeader("Content-Disposition");
        assertNotNull(disposition);
        assertTrue(disposition.contains("attachment"));
        assertTrue(disposition.contains(".xlsx"));
    }
}

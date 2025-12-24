package com.chatlabs.cdev.integration;

import com.alibaba.excel.EasyExcel;
import com.chatlabs.cdev.ExcelIOApplication;
import com.chatlabs.cdev.example.dto.UserDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Excel导入集成测试
 */
@SpringBootTest(classes = ExcelIOApplication.class)
@AutoConfigureMockMvc
@DisplayName("Excel导入集成测试")
class ExcelImportIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("导入正常Excel文件")
    void testImportNormalExcel() throws Exception {
        List<UserDTO> users = createTestUsers(5);
        byte[] excelContent = createExcelContent(users);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "users.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                excelContent
        );

        mockMvc.perform(multipart("/example/users/import").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("成功导入 5 条用户数据"));
    }

    @Test
    @DisplayName("导入空Excel文件")
    void testImportEmptyExcel() throws Exception {
        byte[] excelContent = createExcelContent(List.of());

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                excelContent
        );

        mockMvc.perform(multipart("/example/users/import").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("成功导入 0 条用户数据"));
    }

    @Test
    @DisplayName("导入大量数据")
    void testImportLargeData() throws Exception {
        List<UserDTO> users = createTestUsers(100);
        byte[] excelContent = createExcelContent(users);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "large.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                excelContent
        );

        mockMvc.perform(multipart("/example/users/import").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("成功导入 100 条用户数据"));
    }

    private List<UserDTO> createTestUsers(int count) {
        List<UserDTO> users = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            UserDTO user = new UserDTO();
            user.setId((long) i);
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@test.com");
            user.setAge(20 + i);
            user.setCreateTime(LocalDateTime.now());
            users.add(user);
        }
        return users;
    }

    private byte[] createExcelContent(List<UserDTO> users) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, UserDTO.class).sheet("Sheet1").doWrite(users);
        return outputStream.toByteArray();
    }
}

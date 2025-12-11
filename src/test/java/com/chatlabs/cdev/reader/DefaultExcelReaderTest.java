package com.chatlabs.cdev.reader;

import com.alibaba.excel.EasyExcel;
import com.chatlabs.cdev.example.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DefaultExcelReader 测试
 * 
 * @author DD
 */
class DefaultExcelReaderTest {
    
    private DefaultExcelReader reader;
    
    @BeforeEach
    void setUp() {
        reader = new DefaultExcelReader();
    }
    
    @Test
    void testRead() throws Exception {
        // 先创建一个 Excel 文件
        List<UserDTO> users = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            UserDTO user = new UserDTO();
            user.setId((long) i);
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@test.com");
            user.setAge(20 + i);
            user.setCreateTime(LocalDateTime.now());
            users.add(user);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, UserDTO.class)
                .sheet("test")
                .doWrite(users);
        
        // 读取 Excel
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        List<UserDTO> result = reader.read(inputStream, UserDTO.class);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("user1", result.get(0).getUsername());
    }
}


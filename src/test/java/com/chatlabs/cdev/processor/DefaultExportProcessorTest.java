package com.chatlabs.cdev.processor;

import com.chatlabs.cdev.example.dto.UserDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DefaultExportProcessor 测试
 * 
 * @author DD
 */
class DefaultExportProcessorTest {
    
    private DefaultExportProcessor processor;
    
    @Mock
    private HttpServletResponse response;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        processor = new DefaultExportProcessor();
    }
    
    @Test
    void testProcess() throws Exception {
        // 准备测试数据
        List<UserDTO> users = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            UserDTO user = new UserDTO();
            user.setId((long) i);
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@test.com");
            user.setAge(20 + i);
            user.setCreateTime(LocalDateTime.now());
            users.add(user);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // Mock response
        org.mockito.Mockito.when(response.getOutputStream())
                .thenReturn(new jakarta.servlet.ServletOutputStream() {
                    @Override
                    public boolean isReady() {
                        return true;
                    }
                    
                    @Override
                    public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
                    }
                    
                    @Override
                    public void write(int b) {
                        outputStream.write(b);
                    }
                });
        
        // 执行导出
        processor.process(users, UserDTO.class, "test", "Sheet1", response);
        
        // 验证输出流有数据
        assertTrue(outputStream.size() > 0);
    }
}


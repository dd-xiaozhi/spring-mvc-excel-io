package com.chatlabs.cdev.util;

import com.chatlabs.cdev.dto.TestUserDTO;
import com.chatlabs.cdev.exception.ExcelReadException;
import com.chatlabs.cdev.exception.ExcelWriteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * ExcelUtils 单元测试
 */
@DisplayName("ExcelUtils 单元测试")
class ExcelUtilsTest {

    @Test
    @DisplayName("写入并读取Excel")
    void testWriteAndRead() {
        List<TestUserDTO> data = Arrays.asList(
                new TestUserDTO(1L, "张三", "zhangsan@test.com", 25),
                new TestUserDTO(2L, "李四", "lisi@test.com", 30)
        );

        // 写入
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExcelUtils.write(outputStream, TestUserDTO.class, data, "测试Sheet");

        // 读取
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        List<TestUserDTO> result = ExcelUtils.read(inputStream, TestUserDTO.class);

        // 验证
        assertEquals(2, result.size());
        assertEquals("张三", result.get(0).getName());
        assertEquals(30, result.get(1).getAge());
    }

    @Test
    @DisplayName("读取指定Sheet")
    void testReadSpecificSheet() {
        List<TestUserDTO> data = List.of(new TestUserDTO(1L, "测试", "test@test.com", 20));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExcelUtils.write(outputStream, TestUserDTO.class, data, "Sheet1");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        List<TestUserDTO> result = ExcelUtils.read(inputStream, TestUserDTO.class, 0);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("读取无效输入流返回空列表")
    void testReadEmptyInputStream() {
        // 空流会返回空列表
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExcelUtils.write(out, TestUserDTO.class, List.of(), "Sheet1");
        List<TestUserDTO> result = ExcelUtils.read(new ByteArrayInputStream(out.toByteArray()), TestUserDTO.class);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("工具类不能实例化")
    void testCannotInstantiate() throws Exception {
        var constructor = ExcelUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            fail("应该抛出异常");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof UnsupportedOperationException);
        }
    }
}

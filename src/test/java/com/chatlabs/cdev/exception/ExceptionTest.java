package com.chatlabs.cdev.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 异常类单元测试
 */
@DisplayName("异常类单元测试")
class ExceptionTest {

    @Test
    @DisplayName("ExcelIOException - 消息构造")
    void testExcelIOExceptionWithMessage() {
        ExcelIOException e = new ExcelIOException("测试消息");
        assertEquals("测试消息", e.getMessage());
        assertNull(e.getCause());
    }

    @Test
    @DisplayName("ExcelIOException - 消息和原因构造")
    void testExcelIOExceptionWithMessageAndCause() {
        RuntimeException cause = new RuntimeException("原因");
        ExcelIOException e = new ExcelIOException("测试消息", cause);
        assertEquals("测试消息", e.getMessage());
        assertEquals(cause, e.getCause());
    }

    @Test
    @DisplayName("ExcelIOException - 原因构造")
    void testExcelIOExceptionWithCause() {
        RuntimeException cause = new RuntimeException("原因");
        ExcelIOException e = new ExcelIOException(cause);
        assertEquals(cause, e.getCause());
    }

    @Test
    @DisplayName("ExcelReadException继承ExcelIOException")
    void testExcelReadExceptionInheritance() {
        ExcelReadException e = new ExcelReadException("读取错误");
        assertInstanceOf(ExcelIOException.class, e);
        assertEquals("读取错误", e.getMessage());
    }

    @Test
    @DisplayName("ExcelReadException - 带原因")
    void testExcelReadExceptionWithCause() {
        RuntimeException cause = new RuntimeException("原因");
        ExcelReadException e = new ExcelReadException("读取错误", cause);
        assertEquals("读取错误", e.getMessage());
        assertEquals(cause, e.getCause());
    }

    @Test
    @DisplayName("ExcelWriteException继承ExcelIOException")
    void testExcelWriteExceptionInheritance() {
        ExcelWriteException e = new ExcelWriteException("写入错误");
        assertInstanceOf(ExcelIOException.class, e);
        assertEquals("写入错误", e.getMessage());
    }

    @Test
    @DisplayName("ExcelWriteException - 带原因")
    void testExcelWriteExceptionWithCause() {
        RuntimeException cause = new RuntimeException("原因");
        ExcelWriteException e = new ExcelWriteException("写入错误", cause);
        assertEquals("写入错误", e.getMessage());
        assertEquals(cause, e.getCause());
    }

    @Test
    @DisplayName("ExcelParseException继承ExcelIOException")
    void testExcelParseExceptionInheritance() {
        ExcelParseException e = new ExcelParseException("解析错误");
        assertInstanceOf(ExcelIOException.class, e);
        assertEquals("解析错误", e.getMessage());
    }

    @Test
    @DisplayName("ExcelParseException - 带原因")
    void testExcelParseExceptionWithCause() {
        RuntimeException cause = new RuntimeException("原因");
        ExcelParseException e = new ExcelParseException("解析错误", cause);
        assertEquals("解析错误", e.getMessage());
        assertEquals(cause, e.getCause());
    }
}

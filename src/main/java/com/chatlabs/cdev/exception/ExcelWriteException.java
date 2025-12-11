package com.chatlabs.cdev.exception;

/**
 * Excel 写入异常
 * 
 * @author DD
 */
public class ExcelWriteException extends ExcelIOException {
    
    public ExcelWriteException(String message) {
        super(message);
    }
    
    public ExcelWriteException(String message, Throwable cause) {
        super(message, cause);
    }
}


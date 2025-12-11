package com.chatlabs.cdev.exception;

/**
 * Excel 读取异常
 * 
 * @author DD
 */
public class ExcelReadException extends ExcelIOException {
    
    public ExcelReadException(String message) {
        super(message);
    }
    
    public ExcelReadException(String message, Throwable cause) {
        super(message, cause);
    }
}


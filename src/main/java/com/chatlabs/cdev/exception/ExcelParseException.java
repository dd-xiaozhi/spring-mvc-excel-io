package com.chatlabs.cdev.exception;

/**
 * Excel 解析异常
 * 
 * @author DD
 */
public class ExcelParseException extends ExcelIOException {
    
    public ExcelParseException(String message) {
        super(message);
    }
    
    public ExcelParseException(String message, Throwable cause) {
        super(message, cause);
    }
}


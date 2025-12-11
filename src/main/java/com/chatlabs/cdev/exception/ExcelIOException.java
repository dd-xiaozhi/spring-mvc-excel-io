package com.chatlabs.cdev.exception;

/**
 * Excel IO 异常
 * 
 * @author DD
 */
public class ExcelIOException extends RuntimeException {
    
    public ExcelIOException(String message) {
        super(message);
    }
    
    public ExcelIOException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ExcelIOException(Throwable cause) {
        super(cause);
    }
}


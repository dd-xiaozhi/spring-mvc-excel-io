package com.chatlabs.cdev.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Excel IO 配置属性
 * 
 * @author DD
 */
@Data
@ConfigurationProperties(prefix = "chatlabs.excel.io")
public class ExcelIOProperties {
    
    /** 是否启用 */
    private boolean enabled = true;
    
    /** 默认文件名 */
    private String defaultFileName = "export";
    
    /** 默认 Sheet 名称 */
    private String defaultSheetName = "Sheet1";
    
    /** 日期格式 */
    private String dateFormat = "yyyy-MM-dd HH:mm:ss";
    
    /** 最大文件大小（字节），默认10MB */
    private long maxFileSize = 10 * 1024 * 1024;
    
    /** 异步核心线程数 */
    private int asyncCorePoolSize = 2;
    
    /** 异步最大线程数 */
    private int asyncMaxPoolSize = 5;
    
    /** 异步队列容量 */
    private int asyncQueueCapacity = 100;
}


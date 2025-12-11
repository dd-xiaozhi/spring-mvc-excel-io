package com.chatlabs.cdev.example.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户数据 DTO 示例
 * 
 * @author DD
 */
@Data
public class UserDTO {
    
    @ExcelProperty("用户ID")
    private Long id;
    
    @ExcelProperty("用户名")
    private String username;
    
    @ExcelProperty("邮箱")
    private String email;
    
    @ExcelProperty("年龄")
    private Integer age;
    
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;
}


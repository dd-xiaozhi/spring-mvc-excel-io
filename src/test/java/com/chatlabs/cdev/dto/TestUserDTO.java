package com.chatlabs.cdev.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 测试用 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestUserDTO {
    
    @ExcelProperty("ID")
    private Long id;
    
    @ExcelProperty("姓名")
    private String name;
    
    @ExcelProperty("邮箱")
    private String email;
    
    @ExcelProperty("年龄")
    private Integer age;
}

package com.chatlabs.cdev.annotation;

import com.chatlabs.cdev.reader.DefaultExcelReader;
import com.chatlabs.cdev.reader.ExcelReader;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel 导入注解
 * 标注在 Controller 方法参数上，自动将上传的 Excel 文件转换为对象列表
 * 
 * @author DD
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelImport {
    
    /**
     * 表单字段名，用于获取上传的文件
     */
    String value() default "file";
    
    /**
     * 数据对应的实体类
     */
    Class<?> dataClass();
    
    /**
     * 是否必需，为 true 时文件不存在会抛出异常
     */
    boolean required() default true;
    
    /**
     * 自定义读取器，用于扩展读取逻辑
     * 默认使用 DefaultExcelReader
     */
    Class<? extends ExcelReader> reader() default DefaultExcelReader.class;
}


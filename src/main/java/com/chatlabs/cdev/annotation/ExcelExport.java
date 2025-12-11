package com.chatlabs.cdev.annotation;

import com.chatlabs.cdev.processor.DefaultExportProcessor;
import com.chatlabs.cdev.processor.ExportDataProcessor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Excel 导出注解
 * 标注在 Controller 方法上，自动将方法返回的数据导出为 Excel
 * 
 * @author DD
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelExport {
    
    /**
     * 导出路径，支持路径变量
     * 为空时默认在原路径后加 /export
     * 例如：原路径 /user/list，导出路径为 /user/list/export
     * 可自定义：value = "/user/export" 或 value = "/user/{id}/export"
     */
    String value() default "";
    
    /**
     * Excel 文件名（不含扩展名）
     */
    String fileName() default "export";
    
    /**
     * Sheet 名称
     */
    String sheetName() default "Sheet1";
    
    /**
     * 数据对应的实体类
     */
    Class<?> dataClass();
    
    /**
     * 自定义数据处理器，用于扩展导出逻辑（如邮件发送）
     * 默认使用 DefaultExportProcessor 直接响应文件
     */
    Class<? extends ExportDataProcessor> processor()
        default DefaultExportProcessor.class;
    
    /**
     * 是否复用当前接口方法的逻辑获取数据
     * true: 执行原方法并使用其返回值
     * false: 不执行原方法，由自定义处理器提供数据
     */
    boolean reuseMethod() default true;
    
    /**
     * 是否异步导出
     * true: 异步执行导出任务
     * false: 同步执行（默认）
     */
    boolean async() default false;
}


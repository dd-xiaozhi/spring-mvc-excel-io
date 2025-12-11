# 快速开始指南

## 5 分钟快速上手

### 1. 引入依赖

```xml
<dependency>
    <groupId>com.chatlabs.cdev</groupId>
    <artifactId>spring-mvc-excel-io</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 定义数据类

```java
@Data
public class UserDTO {
    @ExcelProperty("用户ID")
    private Long id;
    
    @ExcelProperty("用户名")
    private String username;
    
    @ExcelProperty("邮箱")
    private String email;
}
```

### 3. 导出 Excel

```java
@RestController
public class UserController {
    
    // 一行注解，搞定导出
    @GetMapping("/users/export")
    @ExcelExport(fileName = "用户列表", dataClass = UserDTO.class)
    public List<UserDTO> export() {
        return userService.list(); // 复用现有接口逻辑
    }
}
```

访问：`http://localhost:8080/users/export` → 自动下载 Excel 文件

### 4. 导入 Excel

```java
@RestController
public class UserController {
    
    // 一个注解，自动解析
    @PostMapping("/users/import")
    public String importUsers(
        @ExcelImport(dataClass = UserDTO.class) List<UserDTO> users
    ) {
        userService.batchSave(users);
        return "导入成功 " + users.size() + " 条";
    }
}
```

使用 Postman 上传 Excel 文件即可。

## 核心特性

### 🎯 声明式编程
通过注解即可实现功能，无需编写样板代码

### 🔌 开箱即用
Spring Boot 自动配置，零配置启动

### 🚀 高性能
基于 EasyExcel，支持大文件流式处理

### 🔧 灵活扩展
- 自定义导出处理器（邮件、OSS 等）
- 自定义读取器
- 自定义响应解析器

### 📊 完善监控
- 详细的日志记录
- 性能指标统计
- 异常信息完整

## 配置选项

在 `application.yml` 中配置：

```yaml
excel:
  io:
    enabled: true                    # 是否启用
    max-file-size: 10485760         # 最大文件 10MB
    date-format: yyyy-MM-dd HH:mm:ss
```

## 高级用法

### 自定义导出处理器

```java
@Component
public class EmailExportProcessor implements ExportDataProcessor {
    @Override
    public void process(List<?> data, Class<?> dataClass, 
                       String fileName, String sheetName,
                       HttpServletResponse response) {
        // 生成 Excel 并发送邮件
    }
}
```

使用：
```java
@ExcelExport(
    fileName = "报表",
    dataClass = UserDTO.class,
    processor = EmailExportProcessor.class  // 指定处理器
)
```

### 自定义响应解析器

如果你的接口返回 `Result<List<T>>`：

```java
@Component
public class ResultResponseUnwrapper implements ResponseUnwrapper {
    @Override
    public boolean supports(Object result) {
        return result instanceof Result;
    }
    
    @Override
    public Object unwrap(Object result) {
        return ((Result<?>) result).getData();
    }
    
    @Override
    public int getOrder() {
        return 100; // 优先级
    }
}
```

然后接口可以直接返回包装类型：
```java
@ExcelExport(fileName = "用户", dataClass = UserDTO.class)
public Result<List<UserDTO>> export() {
    return Result.success(userService.list());
}
```

## 常见问题

### Q: 支持哪些 Excel 格式？
A: 支持 `.xlsx` 格式（Excel 2007+）

### Q: 如何处理大文件？
A: EasyExcel 内部使用流式处理，可处理百万级数据

### Q: 是否支持多 Sheet？
A: 当前版本支持单 Sheet，多 Sheet 功能在规划中

### Q: 如何自定义样式？
A: 可通过自定义处理器实现，参考 EasyExcel 文档

### Q: 导入时如何校验数据？
A: 可在 Controller 中对解析后的 List 进行校验

## 示例项目

完整示例代码位于：
- `com.chatlabs.cdev.example.controller.ExcelExampleController`
- `com.chatlabs.cdev.example.dto.UserDTO`

启动项目后访问：
- 导出：http://localhost:8080/example/users/export
- 导入：POST http://localhost:8080/example/users/import

## 技术支持

- 📖 完整文档：[README.md](../README.md)
- 🏗️ 架构设计：[ARCHITECTURE.md](ARCHITECTURE.md)
- 🧪 测试指南：[TESTING.md](TESTING.md)
- 📊 优化记录：[OPTIMIZATION.md](OPTIMIZATION.md)
- ✅ 评审报告：[REVIEW.md](REVIEW.md)

## License

MIT License

---

**开始使用吧！只需两个注解，Excel 导入导出不再是问题！** 🎉


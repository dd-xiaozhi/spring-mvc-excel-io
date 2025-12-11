# Spring MVC Excel IO

基于 Spring Boot 3 的 Excel 导入导出工具类库，通过注解即可实现 Excel 文件的读写功能。

## 🎯 核心特性

- ✅ **独立导出路径**：自动生成导出路径，无需重复编写接口
- ✅ **异步导出支持**：大数据量异步处理，不阻塞主线程
- ✅ **声明式编程**：基于注解的 Excel 导入导出
- ✅ **自定义处理器**：支持邮件、OSS 等扩展
- ✅ **响应类解析**：自动解析包装的返回值
- ✅ **多种输入类型**：MultipartFile、InputStream、byte[]
- ✅ **开箱即用**：Spring Boot 自动配置
- ✅ **灵活扩展**：策略模式 + 责任链模式

## 技术栈

- Spring Boot 3.3.4
- Java 21
- FastExcel 1.0.1
- Lombok

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.chatlabs.cdev</groupId>
    <artifactId>spring-mvc-excel-io</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

### 2. 定义数据实体

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

### 3. Excel 导出

```java
@RestController
public class UserController {
    
    @GetMapping("/users/export")
    @ExcelExport(
        fileName = "用户列表",
        sheetName = "用户数据",
        dataClass = UserDTO.class
    )
    public List<UserDTO> exportUsers() {
        return userService.list();
    }
}
```

### 4. Excel 导入

```java
@PostMapping("/users/import")
public String importUsers(
    @ExcelImport(value = "file", dataClass = UserDTO.class) 
    List<UserDTO> users
) {
    userService.batchSave(users);
    return "导入成功";
}
```

## 核心注解

### @ExcelExport

用于导出 Excel，标注在 Controller 方法上。

**参数：**
- `fileName`：文件名（不含扩展名），默认 "export"
- `sheetName`：Sheet 名称，默认 "Sheet1"
- `dataClass`：数据实体类（必需）
- `processor`：自定义处理器类，默认直接响应文件
- `reuseMethod`：是否复用原方法逻辑，默认 true

### @ExcelImport

用于导入 Excel，标注在 Controller 方法参数上。

**参数：**
- `value`：表单字段名，默认 "file"
- `dataClass`：数据实体类（必需）
- `required`：是否必需，默认 true
- `reader`：自定义读取器类，默认使用 DefaultExcelReader

## 扩展功能

### 1. 自定义导出处理器

实现 `ExportDataProcessor` 接口，例如导出后发送邮件：

```java
@Component
public class EmailExportProcessor implements ExportDataProcessor {
    
    @Override
    public void process(List<?> data, Class<?> dataClass, 
                       String fileName, String sheetName,
                       HttpServletResponse response) throws Exception {
        // 生成 Excel
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, dataClass)
                .sheet(sheetName)
                .doWrite(data);
        
        // 发送邮件
        emailService.sendWithAttachment(
            "user@example.com",
            fileName + ".xlsx",
            outputStream.toByteArray()
        );
    }
}
```

使用：

```java
@ExcelExport(
    fileName = "用户列表",
    dataClass = UserDTO.class,
    processor = EmailExportProcessor.class
)
```

### 2. 自定义响应解析器

如果你的接口返回包装类型（如 `Result<List<UserDTO>>`），可以实现 `ResponseUnwrapper`：

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

### 3. 自定义 Excel 读取器

实现 `ExcelReader` 接口：

```java
@Component
public class CustomExcelReader implements ExcelReader {
    
    @Override
    public <T> List<T> read(InputStream inputStream, Class<T> dataClass) {
        // 自定义读取逻辑
        return EasyExcel.read(inputStream)
                .head(dataClass)
                .sheet(0)
                .doReadSync();
    }
}
```

## 配置项

在 `application.yml` 中配置：

```yaml
chatlabs:
  excel:
    io:
      # 基础配置
      enabled: true                        # 是否启用，默认 true
      default-file-name: export            # 默认文件名
      default-sheet-name: Sheet1           # 默认 Sheet 名称
      date-format: yyyy-MM-dd HH:mm:ss    # 日期格式
      max-file-size: 10485760              # 最大文件大小（字节）
      
      # 异步导出配置
      async-core-pool-size: 2              # 核心线程数
      async-max-pool-size: 5               # 最大线程数
      async-queue-capacity: 100            # 队列容量
```

## 示例代码

项目包含完整的示例代码，位于：
- `com.chatlabs.cdev.example.controller.ExcelExampleController`
- `com.chatlabs.cdev.example.dto.UserDTO`

启动应用后访问：
- JSON 查询：`GET http://localhost:8080/example/users`
- Excel 导出：`GET http://localhost:8080/example/users/export`（自动生成）
- 异步导出：`GET http://localhost:8080/example/users/async/export`
- Excel 导入：`POST http://localhost:8080/example/users/import`（multipart/form-data）

## 📚 完整文档

- 🚀 [快速开始](doc/QUICKSTART.md) - 5分钟上手指南
- 🆕 [新功能说明](doc/NEW_FEATURES.md) - 独立导出路径 + 异步支持
- 🏗️ [架构设计](doc/ARCHITECTURE.md) - 设计原理和流程图
- 🧪 [测试指南](doc/TESTING.md) - 测试用例和扩展示例
- 📊 [优化记录](doc/OPTIMIZATION.md) - 性能优化和改进
- ✅ [评审报告](doc/REVIEW.md) - 质量评估
- 🎉 [项目总结](doc/SUMMARY.md) - 完整总结

## 架构设计

```
┌─────────────────────────────────────────────────────────┐
│                    Controller Layer                      │
│  @ExcelExport / @ExcelImport                            │
└──────────────────┬──────────────────────────────────────┘
                   │
         ┌─────────┴─────────┐
         │                   │
    ┌────▼─────┐      ┌─────▼────────┐
    │   AOP    │      │  Argument    │
    │  Aspect  │      │  Resolver    │
    └────┬─────┘      └─────┬────────┘
         │                   │
    ┌────▼─────┐      ┌─────▼────────┐
    │ Export   │      │  Excel       │
    │Processor │      │  Reader      │
    └──────────┘      └──────────────┘
         │                   │
         └─────────┬─────────┘
                   │
            ┌──────▼──────┐
            │  FastExcel  │
            └─────────────┘
```

## 设计模式

- **策略模式**：不同的处理器和读取器
- **责任链模式**：响应解析器链
- **模板方法**：Excel 操作流程
- **AOP**：导出功能拦截
- **自定义参数解析器**：导入功能实现

## 测试

运行测试：

```bash
mvn test
```

## License
MIT License

## 作者
DD

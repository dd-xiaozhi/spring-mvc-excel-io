# 测试接口文档

## 启动应用

```bash
mvn spring-boot:run
```

应用将在 http://localhost:8080 启动

## 测试接口

### 1. 导出用户列表

**请求：**
```bash
curl -X GET "http://localhost:8080/example/users/export" \
  -o users.xlsx
```

**说明：**
- 导出 10 条用户数据到 Excel 文件
- 文件名：用户列表.xlsx
- Sheet 名：用户数据

### 2. 条件导出

**请求：**
```bash
curl -X GET "http://localhost:8080/example/users/export-filtered?minAge=25" \
  -o filtered_users.xlsx
```

**说明：**
- 导出年龄 >= 25 的用户
- 演示如何根据查询参数过滤数据

### 3. 导入用户列表

**请求：**
```bash
# 先导出一个文件
curl -X GET "http://localhost:8080/example/users/export" -o test.xlsx

# 再导入该文件
curl -X POST "http://localhost:8080/example/users/import" \
  -F "file=@test.xlsx"
```

**响应：**
```
成功导入 10 条用户数据
```

### 4. 普通查询接口（返回 JSON）

**请求：**
```bash
curl -X GET "http://localhost:8080/example/users" \
  -H "Accept: application/json"
```

**响应：**
```json
[
  {
    "id": 1,
    "username": "user1",
    "email": "user1@example.com",
    "age": 21,
    "createTime": "2025-12-11T14:30:00"
  },
  ...
]
```

## 使用 Postman 测试

### 导出接口
1. 新建请求：GET http://localhost:8080/example/users/export
2. 点击 "Send and Download"
3. 保存为 .xlsx 文件

### 导入接口
1. 新建请求：POST http://localhost:8080/example/users/import
2. Body -> form-data
3. Key: file, Type: File
4. 选择 Excel 文件
5. 点击 Send

## 扩展示例

### 自定义导出处理器（发送邮件）

```java
@Component
public class EmailExportProcessor implements ExportDataProcessor {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Override
    public void process(List<?> data, Class<?> dataClass, 
                       String fileName, String sheetName,
                       HttpServletResponse response) throws Exception {
        // 生成 Excel 到内存
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, dataClass)
                .sheet(sheetName)
                .doWrite(data);
        
        // 发送邮件
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo("user@example.com");
        helper.setSubject("数据导出 - " + fileName);
        helper.setText("请查收附件");
        helper.addAttachment(fileName + ".xlsx", 
            new ByteArrayResource(outputStream.toByteArray()));
        
        mailSender.send(message);
        
        // 给前端返回提示
        response.setContentType("text/plain");
        response.getWriter().write("Excel 已发送到邮箱");
    }
}
```

**使用：**
```java
@GetMapping("/users/export-email")
@ExcelExport(
    fileName = "用户列表",
    dataClass = UserDTO.class,
    processor = EmailExportProcessor.class
)
public List<UserDTO> exportToEmail() {
    return userService.list();
}
```

### 自定义响应解析器

假设你的接口返回 `Result<List<UserDTO>>`：

```java
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
}
```

创建解析器：

```java
@Component
public class ResultResponseUnwrapper implements ResponseUnwrapper {
    
    @Override
    public boolean supports(Object result) {
        return result instanceof Result;
    }
    
    @Override
    public Object unwrap(Object result) {
        Result<?> r = (Result<?>) result;
        return r.getData();
    }
    
    @Override
    public int getOrder() {
        return 100; // 优先级高于默认解析器
    }
}
```

然后接口可以这样写：

```java
@GetMapping("/users/export")
@ExcelExport(fileName = "用户", dataClass = UserDTO.class)
public Result<List<UserDTO>> export() {
    return Result.success(userService.list());
}
```

### 支持多种输入类型

```java
// 1. MultipartFile 类型
@PostMapping("/import1")
public String import1(@ExcelImport(dataClass = UserDTO.class) MultipartFile file) {
    // file 是原始的 MultipartFile 对象
    return "OK";
}

// 2. InputStream 类型
@PostMapping("/import2")
public String import2(@ExcelImport(dataClass = UserDTO.class) InputStream inputStream) {
    // inputStream 是文件的输入流
    return "OK";
}

// 3. byte[] 类型
@PostMapping("/import3")
public String import3(@ExcelImport(dataClass = UserDTO.class) byte[] bytes) {
    // bytes 是文件的字节数组
    return "OK";
}

// 4. List 类型（自动解析）
@PostMapping("/import4")
public String import4(@ExcelImport(dataClass = UserDTO.class) List<UserDTO> users) {
    // users 是已解析的对象列表
    return "Imported " + users.size() + " records";
}
```


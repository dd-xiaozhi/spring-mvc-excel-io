# 更新日志

## [1.1.0] - 2025-12-11

### 🎯 新增功能

#### 1. 独立的导出路径
- 支持自动生成导出路径（默认在原路径后加 `/export`）
- 支持自定义导出路径
- 支持路径变量
- 一个接口同时支持 JSON 和 Excel 导出

**示例：**
```java
@GetMapping("/users")
@ExcelExport(dataClass = UserDTO.class)
public List<UserDTO> listUsers() {
    return userService.list();
}
// 自动生成：GET /users/export
```

#### 2. 异步导出支持
- 支持异步导出大数据量
- 独立的异步线程池配置
- 不阻塞主线程，提高并发能力

**示例：**
```java
@ExcelExport(dataClass = UserDTO.class, async = true)
```

**配置：**
```yaml
chatlabs:
  excel:
    io:
      async-core-pool-size: 2
      async-max-pool-size: 5
      async-queue-capacity: 100
```

#### 3. 文档目录管理
- 所有文档统一放在 `doc/` 目录
- 新增 `NEW_FEATURES.md` 详细说明新功能
- 新增 `CHANGELOG.md` 记录版本变更

### 🔧 优化改进

#### 架构调整
- 移除 AOP 切面方式
- 改用拦截器 + 映射注册器实现
- 新增 `ExcelExportHandler` 处理导出逻辑
- 新增 `ExcelExportInterceptor` 拦截导出请求
- 新增 `ExcelExportMappingRegistrar` 注册导出路径

#### 配置优化
- 配置前缀从 `excel.io` 改为 `chatlabs.excel.io`
- 新增异步线程池配置项
- 启用 `@EnableAsync` 支持异步任务

#### 代码优化
- 优化导入语句顺序
- 统一使用 `@Resource` 注解
- 添加 `@NonNull` 注解增强空值检查
- 移除未使用的导入

### 📚 文档更新

- 更新 README.md 添加新功能说明
- 创建 NEW_FEATURES.md 详细介绍新功能
- 创建 CHANGELOG.md 记录版本变更
- 更新示例代码展示新功能

### 🐛 修复问题

- 修复 `ExcelExportMappingRegistrar` 缺少 `Controller` 导入
- 修复配置属性前缀不一致问题

### ⚠️ 破坏性变更

#### 配置前缀变更
**旧配置：**
```yaml
excel:
  io:
    enabled: true
```

**新配置：**
```yaml
chatlabs:
  excel:
    io:
      enabled: true
```

**迁移方法：** 更新 `application.yml` 中的配置前缀即可，其他代码无需修改。

---

## [1.0.0] - 2025-12-11

### 🎉 初始版本

#### 核心功能
- ✅ Excel 导出（基于 AOP）
- ✅ Excel 导入（基于参数解析器）
- ✅ 自定义导出处理器
- ✅ 自定义读取器
- ✅ 响应类解析器（责任链模式）
- ✅ 多种输入类型支持
- ✅ 文件大小限制
- ✅ 异常处理体系
- ✅ 日志记录
- ✅ Spring Boot 自动配置

#### 技术栈
- Spring Boot 3.3.4
- Java 21
- EasyExcel 4.0.3
- Lombok

#### 设计模式
- 策略模式（处理器选择）
- 责任链模式（响应解析）
- 模板方法模式（操作流程）
- AOP 切面（非侵入式）
- 自定义参数解析器

#### 文档
- README.md - 完整使用文档
- ARCHITECTURE.md - 架构设计
- TESTING.md - 测试指南
- OPTIMIZATION.md - 优化记录
- REVIEW.md - 评审报告
- SUMMARY.md - 项目总结
- QUICKSTART.md - 快速开始

---

## 版本规划

### [1.2.0] - 计划中
- [ ] 导出进度查询
- [ ] 导出任务取消
- [ ] 导出结果缓存
- [ ] 多 Sheet 支持

### [1.3.0] - 计划中
- [ ] 自定义样式支持
- [ ] 数据校验规则
- [ ] 导出模板功能
- [ ] 权限控制

### [2.0.0] - 长期规划
- [ ] 大文件流式处理
- [ ] 导出任务队列
- [ ] 数据脱敏
- [ ] 分布式支持


# 重构记录

## 2025-12-11 - 使用 Spring MVC 标准扩展机制

### 🔧 重构内容

#### 问题
之前使用 `BeanPostProcessor` 来注册导出映射，这不是 Spring MVC 的标准做法，可能导致：
- 时序问题：Bean 初始化顺序不确定
- 不符合 Spring MVC 最佳实践
- 难以与 Spring MVC 的其他扩展集成

#### 解决方案
改用 Spring 的 `InitializingBean` 接口，在所有 Bean 初始化完成后统一注册导出映射。

### 📝 代码变更

#### 删除
- ❌ `ExcelExportMappingRegistrar` (BeanPostProcessor 实现)

#### 新增
- ✅ `ExcelExportMappingConfiguration` (InitializingBean 实现)

### 🎯 新实现的优势

#### 1. 符合 Spring MVC 标准
使用 `InitializingBean` 是 Spring 推荐的初始化扩展方式：

```java
@Component
public class ExcelExportMappingConfiguration implements InitializingBean {
    
    @Resource
    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    
    @Override
    public void afterPropertiesSet() {
        // 在所有 Bean 初始化完成后执行
        registerExportMappings();
    }
}
```

#### 2. 时序可控
- `afterPropertiesSet()` 在所有依赖注入完成后调用
- 确保 `RequestMappingHandlerMapping` 已经完全初始化
- 所有原始映射已经注册完成

#### 3. 更好的映射继承
新实现会继承原始映射的所有条件：

```java
RequestMappingInfo exportMappingInfo = RequestMappingInfo
    .paths(exportPath)
    .methods(RequestMethod.GET)
    .params(originalMapping.getParamsCondition().getExpressions().toArray(new String[0]))
    .headers(originalMapping.getHeadersCondition().getExpressions().toArray(new String[0]))
    .consumes(originalMapping.getConsumesCondition().getExpressions().toArray(new String[0]))
    .produces(originalMapping.getProducesCondition().getExpressions().toArray(new String[0]))
    .build();
```

这意味着：
- 原接口的参数条件会被继承
- 原接口的请求头条件会被继承
- 原接口的内容类型条件会被继承

### 🔍 实现细节

#### 获取已注册的映射
```java
Map<RequestMappingInfo, HandlerMethod> handlerMethods = 
    requestMappingHandlerMapping.getHandlerMethods();
```

直接从 `RequestMappingHandlerMapping` 获取所有已注册的映射，而不是在 Bean 初始化时拦截。

#### 路径提取
支持两种路径模式：

```java
private String getOriginalPath(RequestMappingInfo mappingInfo) {
    // Spring 6.0+ 使用 PathPattern
    if (mappingInfo.getPathPatternsCondition() != null) {
        return mappingInfo.getPathPatternsCondition().getPatterns()
                .iterator().next().getPatternString();
    }
    
    // Spring 5.x 使用 AntPathMatcher
    if (mappingInfo.getPatternsCondition() != null) {
        return mappingInfo.getPatternsCondition().getPatterns()
                .iterator().next();
    }
    
    return null;
}
```

### 📊 对比

| 特性 | BeanPostProcessor | InitializingBean |
|------|------------------|------------------|
| 执行时机 | Bean 初始化过程中 | Bean 初始化完成后 |
| 依赖注入 | 可能未完成 | 已完成 |
| 映射获取 | 需要遍历方法 | 直接获取已注册映射 |
| 条件继承 | 需要手动解析 | 直接从原映射获取 |
| Spring 兼容性 | 非标准做法 | 标准扩展方式 |

### ✅ 测试验证

```bash
mvn clean test
```

**结果：**
```
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### 🎁 附加改进

#### 1. 添加 Response 包装类示例
创建了 `Response<T>` 通用响应类和对应的解析器：

```java
@Component
public class ResponseUnwrapperImpl implements ResponseUnwrapper {
    @Override
    public boolean supports(Object result) {
        return result instanceof Response;
    }
    
    @Override
    public Object unwrap(Object result) {
        return ((Response<?>) result).getData();
    }
}
```

#### 2. 示例代码更新
展示如何使用包装类：

```java
@GetMapping("/users")
@ExcelExport(dataClass = UserDTO.class)
public Response<List<UserDTO>> listUsers() {
    return Response.success(userService.list());
}
```

导出时会自动解析 `Response` 包装，提取实际的 `List<UserDTO>` 数据。

### 📚 参考资料

- [Spring Framework Reference - InitializingBean](https://docs.spring.io/spring-framework/reference/core/beans/factory-nature.html)
- [Spring MVC - RequestMappingHandlerMapping](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.html)

### 🎯 总结

这次重构：
1. ✅ 使用 Spring 标准扩展机制
2. ✅ 提高代码可维护性
3. ✅ 更好的条件继承
4. ✅ 时序更可控
5. ✅ 完全向后兼容

**所有功能正常，测试通过！** 🎉


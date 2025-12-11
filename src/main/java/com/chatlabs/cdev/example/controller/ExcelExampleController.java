package com.chatlabs.cdev.example.controller;

import com.chatlabs.cdev.annotation.ExcelExport;
import com.chatlabs.cdev.annotation.ExcelImport;
import com.chatlabs.cdev.example.dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel IO 示例 Controller
 * 演示如何使用 @ExcelExport 和 @ExcelImport 注解
 * 
 * @author DD
 */
@RestController
@RequestMapping("/example")
public class ExcelExampleController {
    
    private static final Logger log = LoggerFactory.getLogger(ExcelExampleController.class);
    
    /**
     * 示例 1：复用接口导出（默认路径）
     * 访问：GET /example/users - 返回 JSON（包装后的响应）
     * 访问：GET /example/users/export - 导出 Excel（自动生成）
     * <p>
     * 使用 @ExcelExport 注解，自动在原路径后加 /export
     * 导出时会自动解封装 Response，提取 List 数据
     */
    @GetMapping("/users")
    @ExcelExport(
        fileName = "用户列表",
        sheetName = "用户数据",
        dataClass = UserDTO.class
    )
    public List<UserDTO> listUsers() {
        // 模拟查询用户数据
        List<UserDTO> users = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            UserDTO user = new UserDTO();
            user.setId((long) i);
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            user.setAge(20 + i);
            user.setCreateTime(LocalDateTime.now());
            users.add(user);
        }
        
        log.info("查询用户列表，共 {} 条数据", users.size());
        // 返回 List，ResponseWrapper 会自动封装成 Response
        return users;
    }
    
    /**
     * 示例 2：自定义导出路径
     * 访问：GET /example/users/list - 返回 JSON
     * 访问：GET /example/users/download - 导出 Excel（自定义路径）
     * <p>
     * 通过 value 属性指定自定义导出路径
     */
    @GetMapping("/users/list")
    @ExcelExport(
        value = "/example/users/download",
        fileName = "用户数据",
        sheetName = "用户",
        dataClass = UserDTO.class
    )
    public List<UserDTO> listUsersCustomPath() {
        List<UserDTO> users = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            UserDTO user = new UserDTO();
            user.setId((long) i);
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            user.setAge(25 + i);
            user.setCreateTime(LocalDateTime.now());
            users.add(user);
        }
        
        log.info("查询用户列表（自定义路径），共 {} 条数据", users.size());
        return users;
    }
    
    /**
     * 示例 3：异步导出
     * 访问：GET /example/users/async - 返回 JSON
     * 访问：GET /example/users/async/export - 异步导出 Excel
     * <p>
     * 使用 async = true 启用异步导出
     */
    @GetMapping("/users/async")
    @ExcelExport(
        fileName = "异步用户列表",
        sheetName = "用户数据",
        dataClass = UserDTO.class,
        async = true
    )
    public List<UserDTO> listUsersAsync() {
        List<UserDTO> users = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            UserDTO user = new UserDTO();
            user.setId((long) i);
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            user.setAge(20 + i);
            user.setCreateTime(LocalDateTime.now());
            users.add(user);
        }
        
        log.info("查询用户列表（异步导出），共 {} 条数据", users.size());
        return users;
    }
    
    /**
     * 示例 4：导入用户列表
     * 访问：POST /example/users/import
     * Content-Type: multipart/form-data
     * <p>
     * 使用 @ExcelImport 注解自动解析 Excel 文件
     */
    @PostMapping("/users/import")
    public String importUsers(@ExcelImport(value = "file", dataClass = UserDTO.class) List<UserDTO> users) {
        log.info("导入用户列表，共 {} 条数据", users.size());
        
        // 处理导入的数据
        users.forEach(user -> {
            log.debug("导入用户: {}", user);
            // 这里可以保存到数据库等操作
        });
        
        return "成功导入 " + users.size() + " 条用户数据";
    }
    
    /**
     * 示例 5：条件导出
     * 访问：GET /example/users/filtered?minAge=25 - 返回 JSON
     * 访问：GET /example/users/filtered/export?minAge=25 - 导出 Excel
     * <p>
     * 根据查询参数过滤后导出
     */
    @GetMapping("/users/filtered")
    @ExcelExport(
        fileName = "筛选用户",
        sheetName = "用户数据",
        dataClass = UserDTO.class
    )
    public List<UserDTO> listFilteredUsers(@RequestParam(defaultValue = "0") Integer minAge) {
        List<UserDTO> users = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            UserDTO user = new UserDTO();
            user.setId((long) i);
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@example.com");
            user.setAge(20 + i);
            user.setCreateTime(LocalDateTime.now());
            
            // 根据年龄过滤
            if (user.getAge() >= minAge) {
                users.add(user);
            }
        }
        
        log.info("查询筛选用户，最小年龄 {}，共 {} 条数据", minAge, users.size());
        return users;
    }
}


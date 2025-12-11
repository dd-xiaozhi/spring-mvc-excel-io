package com.chatlabs.cdev.config;

import com.chatlabs.cdev.interceptor.ExcelExportInterceptor;
import com.chatlabs.cdev.resolver.ExcelImportArgumentResolver;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Excel IO 自动配置类
 * 
 * @author DD
 */
@EnableAsync
@EnableConfigurationProperties(ExcelIOProperties.class)
@ConditionalOnProperty(prefix = "chatlabs.excel.io", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.chatlabs.cdev")
public class ExcelIOAutoConfiguration implements WebMvcConfigurer {
    
    private static final Logger log = LoggerFactory.getLogger(ExcelIOAutoConfiguration.class);

    private final ExcelIOProperties properties;
    private final ApplicationContext applicationContext;
    
    @Resource
    private ExcelExportInterceptor excelExportInterceptor;

    public ExcelIOAutoConfiguration(ApplicationContext applicationContext, ExcelIOProperties properties) {
        this.properties = properties;
        this.applicationContext = applicationContext;
        log.info("Excel IO 自动配置已启用");
        log.debug("配置信息: maxFileSize={}bytes, dateFormat={}", 
                properties.getMaxFileSize(), properties.getDateFormat());
    }
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 注册 Excel 导入参数解析器
        resolvers.add(new ExcelImportArgumentResolver(applicationContext, properties));
        log.debug("已注册 ExcelImportArgumentResolver");
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Excel 导出拦截器
        registry.addInterceptor(excelExportInterceptor);
        log.debug("已注册 ExcelExportInterceptor");
    }
    
    /**
     * 配置 Excel 导出异步线程池
     */
    @Bean
    @ConditionalOnMissingBean
    public Executor excelExportExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数
        executor.setCorePoolSize(properties.getAsyncCorePoolSize());
        
        // 最大线程数
        executor.setMaxPoolSize(properties.getAsyncMaxPoolSize());
        
        // 队列容量
        executor.setQueueCapacity(properties.getAsyncQueueCapacity());
        
        // 线程名称前缀
        executor.setThreadNamePrefix("excel-export-");
        
        // 拒绝策略：由调用线程处理
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 等待时间
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        
        log.info("Excel 导出异步线程池已配置: corePoolSize={}, maxPoolSize={}, queueCapacity={}", 
                properties.getAsyncCorePoolSize(), 
                properties.getAsyncMaxPoolSize(), 
                properties.getAsyncQueueCapacity());
        
        return executor;
    }
}


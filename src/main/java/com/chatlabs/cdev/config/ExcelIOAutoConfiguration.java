package com.chatlabs.cdev.config;

import com.chatlabs.cdev.interceptor.ExcelExportInterceptor;
import com.chatlabs.cdev.resolver.ExcelImportArgumentResolver;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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
 * Excel IO 自动配置
 * 
 * @author DD
 */
@Slf4j
@EnableAsync
@EnableConfigurationProperties(ExcelIOProperties.class)
@ConditionalOnProperty(prefix = "chatlabs.excel.io", name = "enabled", havingValue = "true", matchIfMissing = true)
@ComponentScan(basePackages = "com.chatlabs.cdev")
public class ExcelIOAutoConfiguration implements WebMvcConfigurer {

    private final ExcelIOProperties properties;
    private final ApplicationContext applicationContext;
    
    @Resource
    private ExcelExportInterceptor excelExportInterceptor;

    public ExcelIOAutoConfiguration(ApplicationContext applicationContext, ExcelIOProperties properties) {
        this.properties = properties;
        this.applicationContext = applicationContext;
        log.info("Excel IO 已启用, maxFileSize={}MB", properties.getMaxFileSize() / 1024 / 1024);
    }
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ExcelImportArgumentResolver(applicationContext, properties));
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(excelExportInterceptor);
    }
    
    @Bean
    @ConditionalOnMissingBean
    public Executor excelExportExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getAsyncCorePoolSize());
        executor.setMaxPoolSize(properties.getAsyncMaxPoolSize());
        executor.setQueueCapacity(properties.getAsyncQueueCapacity());
        executor.setThreadNamePrefix("excel-export-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        log.debug("异步线程池: core={}, max={}, queue={}", 
                properties.getAsyncCorePoolSize(), 
                properties.getAsyncMaxPoolSize(), 
                properties.getAsyncQueueCapacity());
        return executor;
    }
}

